package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.auth.ProjectDTOMapper;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.AgreementService;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;

@Controller
public class AgreementController {

    private final ProjectService projectService;
    private final AgreementService agreementService;
    private final MusicianService musicianService;

    public AgreementController(ProjectService projectService, AgreementService agreementService, MusicianService musicianService) {
        this.projectService = projectService;
        this.agreementService = agreementService;
        this.musicianService = musicianService;
    }

    @GetMapping("/admin/project/{id}/template/edit")
    public String editTemplateForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDto(project);
        AgreementTemplate template = project.getAgreementTemplate();

        model.addAttribute("project", projectDTO);
        model.addAttribute("templateContent", template.getContent());
        return "admin-edit-template";
    }

    @PostMapping("/admin/project/{id}/template/edit")
    public String updateTemplate(@PathVariable Long id, @RequestParam String templateContent, RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectById(id);
        AgreementTemplate template = project.getAgreementTemplate();

        template.setContent(templateContent);
        agreementService.saveTemplate(template);

        redirectAttributes.addFlashAttribute("success", "Template updated successfully.");
        return "redirect:/admin/project/" + id + "/template/edit";
    }

    @GetMapping("/musician/project/{projectId}/agreement")
    public String viewAgreement(@PathVariable Long projectId, Model model, Authentication authentication) throws AccessDeniedException {
        String email = authentication.getName();
        Musician musician = musicianService.findMusicianByEmail(email);

        Project project = projectService.getProjectById(projectId);

        if (!project.getInvited().contains(musician) && !project.getProjectMembers().contains(musician)) {
            throw new AccessDeniedException("You are not invited to this project.");
        }

        String agreementContent = agreementService.generateAgreementContent(project, musician);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDto(project);

        boolean accepted = project.getProjectMembers().contains(musician);

        model.addAttribute("project", projectDTO);
        model.addAttribute("agreementContent", agreementContent);
        model.addAttribute("hasResponded", accepted);
        model.addAttribute("responseStatus", accepted ? "Accepted" : "Pending");

        return "agreement";
    }
}
