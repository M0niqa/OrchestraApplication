package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.exception.MissingDataException;
import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.MusicianAgreement;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicianAgreementRepository;
import com.monika.worek.orchestra.repository.AgreementTemplateRepository;
import com.monika.worek.orchestra.service.AgreementGenerationService;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.PdfService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AgreementController {

    private final AgreementGenerationService agreementGenerationService;
    private final MusicianService musicianService;
    private final AgreementTemplateRepository agreementTemplateRepository;
    private final PdfService pdfService;
    private final MusicianAgreementRepository musicianAgreementRepository;
    private final ProjectService projectService;

    public AgreementController(AgreementGenerationService agreementGenerationService, MusicianService musicianService, AgreementTemplateRepository agreementTemplateRepository, PdfService pdfService, MusicianAgreementRepository musicianAgreementRepository, ProjectService projectService) {
        this.agreementGenerationService = agreementGenerationService;
        this.musicianService = musicianService;
        this.agreementTemplateRepository = agreementTemplateRepository;
        this.pdfService = pdfService;
        this.musicianAgreementRepository = musicianAgreementRepository;
        this.projectService = projectService;
    }

    @GetMapping("/admin/template/edit")
    public String editTemplateForm(Model model) {
        AgreementTemplate template = agreementTemplateRepository.findById(1L).orElseThrow((() -> new IllegalArgumentException("Template not found")));
        model.addAttribute("templateContent", template.getContent());
        return "/admin/admin-edit-template";
    }

    @PostMapping("/admin/template/edit")
    public String updateTemplate(@RequestParam String templateContent, RedirectAttributes redirectAttributes) {
        AgreementTemplate template = agreementTemplateRepository.findById(1L).orElseThrow((() -> new IllegalArgumentException("Template not found")));

        template.setContent(templateContent);
        agreementTemplateRepository.save(template);

        redirectAttributes.addFlashAttribute("success", "Template updated successfully.");
        return "redirect:/admin/template/edit";
    }

    @GetMapping("/musician/project/{projectId}/downloadAgreement")
    public ResponseEntity<byte[]> downloadAgreement(@PathVariable Long projectId, Authentication auth) {
        try {
            Musician musician = musicianService.getMusicianByEmail(auth.getName());
            byte[] agreement = agreementGenerationService.getOrGenerateAgreement(projectId, musician);

            String filename = musician.getLastName() + "_agreement.pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(agreement);

        } catch (MissingDataException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to generate agreement".getBytes());
        }
    }

    @GetMapping("/admin/project/{projectId}/downloadAgreements")
    public ResponseEntity<byte[]> downloadAllAgreements(@PathVariable Long projectId) {
        try {
            Project project = projectService.getProjectById(projectId);
            project.getProjectMembers().forEach(m -> agreementGenerationService.getOrGenerateAgreement(projectId, m));
            List<MusicianAgreement> agreements = musicianAgreementRepository.findByProjectId(projectId);

            if (agreements.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No agreements found for this project.".getBytes());
            }

            byte[] merged = pdfService.mergePdfFiles(
                    agreements.stream().map(a -> new File(a.getFilePath())).collect(Collectors.toList())
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_agreements.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(merged);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Failed to generate merged PDF: " + e.getMessage()).getBytes());
        }
    }
}
