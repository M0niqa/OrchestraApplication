package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.auth.ProjectBasicInfoDTOMapper;
import com.monika.worek.orchestra.auth.ProjectDTOMapper;
import com.monika.worek.orchestra.dto.InstrumentCountAndSalaryDTO;
import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.AgreementTemplateRepository;
import com.monika.worek.orchestra.service.AgreementService;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {

    private final ProjectService projectService;
    private final AgreementService agreementService;
    private final MusicianService musicianService;

    public ProjectController(ProjectService projectService, AgreementService agreementService, AgreementTemplateRepository agreementTemplateRepository, MusicianService musicianService) {
        this.projectService = projectService;
        this.agreementService = agreementService;
        this.musicianService = musicianService;
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

    @PostMapping("/musician/project/{projectId}/invitation/accept")
    public String acceptInvitation(@PathVariable Long projectId, Authentication authentication) {
        String email = authentication.getName();
        Musician musician = musicianService.findMusicianByEmail(email);
        projectService.acceptInvitation(projectId, musician.getId());
        return "redirect:/musician/project/" + projectId + "/agreement?acceptSuccess";
    }

    @PostMapping("/musician/project/{projectId}/invitation/reject")
    public String rejectInvitation(@PathVariable Long projectId, Authentication authentication) {
        String email = authentication.getName();
        Musician musician = musicianService.findMusicianByEmail(email);
        projectService.rejectInvitation(projectId, musician.getId());
        return "redirect:/musician/project/" + projectId + "/agreement?rejectSuccess";
    }

    @GetMapping("/musician/project/{id}")
    public String viewProjectDetailsForMusician(@PathVariable Long id, Model model) {
        ProjectDTO projectDTO = projectService.getProjectDtoById(id);
        model.addAttribute("project", projectDTO);
        return "musician-project-details";
    }





    @GetMapping("/inspector/project/{projectId}/sendInvitation")
    public String showInviteMusiciansPage(@PathVariable Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);

        Map<Instrument, Integer> remainingCounts = projectService.getRemainingInstrumentsCount(project);

        LinkedHashMap<Instrument, List<MusicianBasicDTO>> musiciansByInstrument =
                projectService.getAvailableMusiciansByInstrument(projectId);

        model.addAttribute("musiciansByInstrument", musiciansByInstrument);
        model.addAttribute("remainingCounts", remainingCounts);
        model.addAttribute("projectId", projectId);

        return "send-invitation";
    }

    @PostMapping("/inspector/project/{projectId}/sendInvitation")
    public String inviteMusicians(@PathVariable Long projectId, @RequestParam(value = "musicianIds", required = false) List<Long> musicianIds, RedirectAttributes redirectAttributes) {
        if (musicianIds != null && !musicianIds.isEmpty()) {
            for (Long musicianId : musicianIds) {
                projectService.inviteMusician(projectId, musicianId);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Invitations sent successfully!");
        return "redirect:/inspector/project/" + projectId + "/sendInvitation";
    }








    @GetMapping("/admin/addProject")
    public String showAddProjectForm(Model model) {
        model.addAttribute("projectDTO", new ProjectBasicInfoDTO());
        return "addProject";
    }

    @PostMapping("/admin/addProject")
    public String addProject(@Valid @ModelAttribute("projectDTO") ProjectBasicInfoDTO projectDTO, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "addProject";
        }
        Project project = ProjectBasicInfoDTOMapper.mapToEntity(projectDTO);
        projectService.saveProject(project);
        redirectAttributes.addFlashAttribute("success", "Project added successfully!");
        return "redirect:/adminPage";
    }

    @GetMapping({"/admin/project/{id}/musicianStatus", "/inspector/project/{id}/musicianStatus"})
    public String showMusicianStatus(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectDTO projectDTO = projectService.getProjectDtoById(id);
        LinkedHashMap<Instrument, List<MusicianBasicDTO>> musiciansByInstrument = projectService.getProjectMembersByInstrument(project);
        model.addAttribute("projectMembersByInstrument", musiciansByInstrument);
        model.addAttribute("refusedMusicians", projectDTO.getMusiciansWhoRejected());
        model.addAttribute("pendingMusicians", projectDTO.getInvited());
        model.addAttribute("project", projectDTO);

        return "musicianStatus";
    }

    @PostMapping("/admin/project/{id}/delete")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProjectById(id);
        return "redirect:/adminPage";
    }

    @GetMapping("/admin/project/{id}")
    public String viewProject(@PathVariable Long id, Model model) {
        ProjectBasicInfoDTO projectBasicDTO = projectService.getProjectBasicDtoById(id);
        model.addAttribute("project", projectBasicDTO);
        return "project-details";
    }

    @PostMapping("/admin/project/{id}/update")
    public String updateProject(@PathVariable Long id, @Valid @ModelAttribute("project") ProjectBasicInfoDTO projectBasicDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "project-details";
        }
        projectService.updateProject(id, projectBasicDTO);
        redirectAttributes.addFlashAttribute("success", "Project updated successfully!");
        return "redirect:/admin/project/" + id;
    }

    @GetMapping("/admin/project/{id}/template/edit")
    public String editTemplateForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDto(project);
        AgreementTemplate template = project.getAgreementTemplate();

        model.addAttribute("project", projectDTO);
        model.addAttribute("templateContent", template.getContent());
        return "edit-template";
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

    @GetMapping("/admin/project/{projectId}/instrumentCount/edit")
    public String showInstrumentConfigForm(@PathVariable Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);

        InstrumentCountAndSalaryDTO instrCountAndSalaryDTO = new InstrumentCountAndSalaryDTO();
        instrCountAndSalaryDTO.setInstrumentCounts(project.getInstrumentCounts());
        instrCountAndSalaryDTO.setGroupSalaries(project.getGroupSalaries());

        model.addAttribute("projectId", projectId);
        model.addAttribute("instruments", Instrument.values());
        model.addAttribute("instrumentGroups", List.of("Strings", "Winds", "Brass", "Solo"));
        model.addAttribute("configDTO", instrCountAndSalaryDTO);
        return "instrument-count";
    }

    @PostMapping("/admin/project/{projectId}/instrumentCount/edit")
    public String updateInstrumentConfig(@PathVariable Long projectId, @ModelAttribute InstrumentCountAndSalaryDTO instrumentConfigDTO, RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectById(projectId);

        project.setInstrumentCounts(instrumentConfigDTO.getInstrumentCounts());
        project.setGroupSalaries(instrumentConfigDTO.getGroupSalaries());

        projectService.saveProject(project);

        redirectAttributes.addFlashAttribute("success", "Instrument configuration updated successfully!");
        return "redirect:/admin/project/" + projectId + "/instrumentCount/edit";
    }

}
