package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.auth.ProjectDTOMapper;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.AgreementTemplateRepository;
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

@Controller
public class AgreementController {

    private final ProjectService projectService;
    private final AgreementService agreementService;
    private final MusicianService musicianService;
    private final AgreementTemplateRepository agreementTemplateRepository;

    public AgreementController(ProjectService projectService, AgreementService agreementService, MusicianService musicianService, AgreementTemplateRepository agreementTemplateRepository) {
        this.projectService = projectService;
        this.agreementService = agreementService;
        this.musicianService = musicianService;
        this.agreementTemplateRepository = agreementTemplateRepository;
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
        agreementService.saveTemplate(template);

        redirectAttributes.addFlashAttribute("success", "Template updated successfully.");
        return "redirect:/admin/template/edit";
    }

    @GetMapping("/musician/project/{projectId}/agreement")
    public String viewAgreement(@PathVariable Long projectId, Model model, Authentication authentication) {
        String email = authentication.getName();
        Musician musician = musicianService.findMusicianByEmail(email);

        Project project = projectService.getProjectById(projectId);
        String agreementContent = agreementService.generateAgreementContent(project, musician);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDto(project);

        boolean accepted = project.getProjectMembers().contains(musician);

        model.addAttribute("project", projectDTO);
        model.addAttribute("agreementContent", agreementContent);
        model.addAttribute("accepted", accepted);

        return "/musician/agreement";
    }
}
