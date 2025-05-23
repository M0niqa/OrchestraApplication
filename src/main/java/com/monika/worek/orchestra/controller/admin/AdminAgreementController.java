package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.MusicianAgreement;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.AgreementService;
import com.monika.worek.orchestra.service.PdfService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class AdminAgreementController {

    private final PdfService pdfService;
    private final ProjectService projectService;
    private final AgreementService agreementService;

    public AdminAgreementController(PdfService pdfService, ProjectService projectService, AgreementService agreementService) {
        this.pdfService = pdfService;
        this.projectService = projectService;
        this.agreementService = agreementService;
    }

    @GetMapping("/admin/template/edit")
    public String editTemplateForm(Model model) {
        AgreementTemplate template = agreementService.findTemplateById(1L);
        model.addAttribute("templateContent", template.getContent());
        return "/admin/admin-edit-template";
    }

    @PostMapping("/admin/template/edit")
    public String updateTemplate(@RequestParam String templateContent, RedirectAttributes redirectAttributes) {
        AgreementTemplate template = agreementService.findTemplateById(1L);

        template.setContent(templateContent);
        agreementService.saveTemplate(template);

        redirectAttributes.addFlashAttribute("success", "Template updated successfully.");
        return "redirect:/admin/template/edit";
    }

    @GetMapping("/admin/project/{projectId}/downloadAgreements")
    public ResponseEntity<byte[]> downloadAllAgreements(@PathVariable Long projectId) {
        try {
            Project project = projectService.getProjectById(projectId);
            project.getProjectMembers().forEach(m -> agreementService.getOrGenerateAgreement(projectId, m));
            List<MusicianAgreement> agreements = agreementService.findAgreementsByProjectId(projectId);

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
