package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.MusicianAgreement;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.AgreementRepository;
import com.monika.worek.orchestra.repository.AgreementTemplateRepository;
import com.monika.worek.orchestra.service.*;
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
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AgreementController {

    private final AgreementGenerationService agreementGenerationService;
    private final MusicianService musicianService;
    private final ProjectService projectService;
    private final AgreementTemplateRepository agreementTemplateRepository;
    private final PdfService pdfService;
    private final AgreementRepository agreementRepository;
    private final FileStorageService fileStorageService;

    public AgreementController(AgreementGenerationService agreementGenerationService, MusicianService musicianService, ProjectService projectService, AgreementTemplateRepository agreementTemplateRepository, PdfService pdfService, AgreementRepository agreementRepository, FileStorageService fileStorageService) {
        this.agreementGenerationService = agreementGenerationService;
        this.musicianService = musicianService;
        this.projectService = projectService;
        this.agreementTemplateRepository = agreementTemplateRepository;
        this.pdfService = pdfService;
        this.agreementRepository = agreementRepository;
        this.fileStorageService = fileStorageService;
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
        agreementGenerationService.saveTemplate(template);

        redirectAttributes.addFlashAttribute("success", "Template updated successfully.");
        return "redirect:/admin/template/edit";
    }

    @GetMapping("/musician/project/{projectId}/downloadAgreement")
    public ResponseEntity<byte[]> downloadAgreement(@PathVariable Long projectId, Authentication authentication) {
        try {
            Musician musician = musicianService.getMusicianByEmail(authentication.getName());

            if (musicianService.isDataMissing(musician)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Your personal or business data is incomplete. Agreement cannot be generated.".getBytes());
            }

            Project project = projectService.getProjectById(projectId);

            Optional<MusicianAgreement> existing = Optional.ofNullable(
                    agreementRepository.findByMusicianIdAndProjectId(projectId, musician.getId()));
            if (existing.isPresent()) {
                return downloadFromPath(existing.get().getFilePath(), existing.get().getFileName());
            }

            String content = agreementGenerationService.generateAgreementContent(project, musician);
            byte[] pdf = pdfService.generatePdfFromText(content);
            String filePath = fileStorageService.saveGeneratedAgreement(pdf, musician, project);

            MusicianAgreement agreement = new MusicianAgreement();
            agreement.setFileName(musician.getLastName() + "_agreement.pdf");
            agreement.setFilePath(filePath);
            agreement.setFileType("application/pdf");
            agreement.setCreatedAt(LocalDateTime.now());
            agreement.setMusician(musician);
            agreement.setProject(project);
            agreementRepository.save(agreement);

            agreementRepository.save(agreement);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + agreement.getFileName())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Failed to generate agreement").getBytes());
        }
    }

    @GetMapping("/admin/project/{projectId}/downloadAgreements")
    public ResponseEntity<byte[]> downloadAllAgreements(@PathVariable Long projectId) {
        try {
            List<MusicianAgreement> agreements = agreementRepository.findByProjectId(projectId);

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

    private ResponseEntity<byte[]> downloadFromPath(String path, String filename) {
        try {
            File file = new File(path);
            byte[] data = Files.readAllBytes(file.toPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(("Failed to read file: " + filename).getBytes());
        }
    }
}
