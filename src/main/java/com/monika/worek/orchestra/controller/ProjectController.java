package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.auth.NewProjectDTOMapper;
import com.monika.worek.orchestra.auth.ProjectDTOMapper;
import com.monika.worek.orchestra.dto.InstrumentCountAndSalaryDTO;
import com.monika.worek.orchestra.dto.NewProjectDTO;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.AgreementTemplateRepository;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import com.monika.worek.orchestra.service.AgreementGenerationService;
import com.monika.worek.orchestra.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final MusicianRepository musicianRepository;
    private final AgreementGenerationService agreementGenerationService;
    private final AgreementTemplateRepository agreementTemplateRepository;

    public ProjectController(ProjectRepository projectRepository, ProjectService projectService, MusicianRepository musicianRepository, AgreementGenerationService agreementGenerationService, AgreementTemplateRepository agreementTemplateRepository) {
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.musicianRepository = musicianRepository;
        this.agreementGenerationService = agreementGenerationService;
        this.agreementTemplateRepository = agreementTemplateRepository;
    }

    @GetMapping("/musician/project/{projectId}/agreement")
    public String viewAgreement(@PathVariable Long projectId, Model model, Authentication authentication) throws AccessDeniedException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Musician musician = musicianRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));

        Project project = projectService.getProjectById(projectId);

        if (!project.getInvited().contains(musician) && !project.getProjectMembers().contains(musician)) {
            throw new AccessDeniedException("You are not invited to this project.");
        }

        String agreementContent = agreementGenerationService.generateAgreementContent(project, musician);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDTO(project);

        boolean accepted = project.getProjectMembers().contains(musician);

        model.addAttribute("project", projectDTO);
        model.addAttribute("agreementContent", agreementContent);
        model.addAttribute("hasResponded", accepted);
        model.addAttribute("responseStatus", accepted ? "Accepted" : "Pending");

        return "agreement";
    }

    @PostMapping("/musician/project/{projectId}/invitation/accept")
    public String acceptInvitation(@PathVariable Long projectId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Musician musician = musicianRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));
        try {
            projectService.acceptInvitation(projectId, musician.getId());
            return "redirect:/musician/project/" + projectId + "/agreement?acceptSuccess";
        } catch (Exception e) {
            return "redirect:/musician/project/" + projectId + "/agreement?error=" + e.getMessage();
        }
    }

    @PostMapping("/musician/project/{projectId}/invitation/reject")
    public String rejectInvitation(@PathVariable Long projectId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Musician musician = musicianRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));
        try {
            projectService.rejectInvitation(projectId, musician.getId());
            return "redirect:/musician/project/" + projectId + "/agreement?rejectSuccess";
        } catch (Exception e) {
            return "redirect:/musician/project/" + projectId + "/agreement?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/musician/project/{id}")
    public String viewProjectDetailsForMusician(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDTO(project);
        model.addAttribute("project", projectDTO);
        return "musician-project-details";
    }





    @GetMapping("/inspector/project/{projectId}/sendInvitation")
    public String showAddMusiciansPage(@PathVariable Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);

        Map<Instrument, Integer> remainingCounts = projectService.getRemainingInstrumentsCount(project);

        LinkedHashMap<Instrument, List<Musician>> musiciansByInstrument =
                projectService.getAvailableMusiciansByInstrument(projectId);

        model.addAttribute("musiciansByInstrument", musiciansByInstrument);
        model.addAttribute("remainingCounts", remainingCounts);
        model.addAttribute("projectId", projectId);

        return "sendInvitation";
    }

    @PostMapping("/inspector/project/{projectId}/sendInvitation")
    public String inviteMusicians(@PathVariable Long projectId,
                                  @RequestParam(value = "musicianIds", required = false) List<Long> musicianIds,
                                  RedirectAttributes redirectAttributes) {
        if (musicianIds != null && !musicianIds.isEmpty()) {
            for (Long musicianId : musicianIds) {
                projectService.inviteMusician(projectId, musicianId);
            }
            redirectAttributes.addFlashAttribute("success", "Invitations sent successfully!");
        }

        return "redirect:/inspector/project/" + projectId + "/sendInvitation";
    }








    @GetMapping("/admin/addProject")
    public String showAddProjectForm(Model model) {
        model.addAttribute("instruments", Instrument.values());
        model.addAttribute("instrumentGroups", List.of("Strings", "Winds", "Brass", "Solo"));
        model.addAttribute("projectDTO", new NewProjectDTO());
        return "addProject";
    }

    @PostMapping("/admin/addProject")
    public String addProject(@Valid @ModelAttribute("projectDTO") NewProjectDTO projectDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("instruments", Instrument.values());
        model.addAttribute("instrumentGroups", List.of("Strings", "Winds", "Brass", "Solo"));
        System.out.println("Project Name: " + projectDTO.getName());
        System.out.println("Start Date: " + projectDTO.getStartDate());
        System.out.println("End Date: " + projectDTO.getEndDate());
        if (bindingResult.hasErrors()) {
            return "addProject";
        }
        Project project = NewProjectDTOMapper.mapToEntity(projectDTO);
        projectRepository.save(project);
        redirectAttributes.addFlashAttribute("success", "Project added successfully!");
        return "redirect:/adminPage";
    }

    @GetMapping({"/admin/project/{id}/musicianStatus", "/inspector/project/{id}/musicianStatus"})
    public String showMusicianStatus(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDTO(project);
        LinkedHashMap<Instrument, List<Musician>> musiciansByInstrument = projectService.getProjectMembersByInstrument(project);
        model.addAttribute("projectMembersByInstrument", musiciansByInstrument);
        model.addAttribute("refusedMusicians", project.getMusiciansWhoRejected());
        model.addAttribute("pendingMusicians", project.getInvited());
        model.addAttribute("project", projectDTO);

        return "musicianStatus";
    }

    @PostMapping("/admin/project/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            projectService.deleteProjectById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Project deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete project.");
        }
        return "redirect:/adminPage";
    }

    @GetMapping("/admin/project/{id}")
    public String viewProject(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDTO(project);
        model.addAttribute("project", projectDTO);
        return "project-details";
    }

    @PostMapping("/admin/project/{id}/update")
    public String updateProject(@PathVariable Long id,
                                @Valid @ModelAttribute("project") ProjectDTO projectDTO, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("projectId", id);
            return "project-details";
        }
        projectService.updateProject(id, projectDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Project updated successfully.");
        return "redirect:/admin/project/" + id;
    }

    @GetMapping("/admin/project/{id}/template/edit")
    public String editTemplateForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDTO(project);
        AgreementTemplate template = project.getAgreementTemplate();

        model.addAttribute("project", projectDTO);
        model.addAttribute("templateContent", template.getContent());
        return "edit-template";
    }

    @PostMapping("/admin/project/{id}/template/edit")
    public String updateTemplate(@PathVariable Long id,
                                 @RequestParam String templateContent,
                                 RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectById(id);
        AgreementTemplate template = project.getAgreementTemplate();

        template.setContent(templateContent);
        agreementTemplateRepository.save(template);

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
    public String updateInstrumentConfig(@PathVariable Long projectId,
                                         @ModelAttribute InstrumentCountAndSalaryDTO configDTO,
                                         RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectById(projectId);

        project.setInstrumentCounts(configDTO.getInstrumentCounts());
        project.setGroupSalaries(configDTO.getGroupSalaries());

        projectRepository.save(project);

        redirectAttributes.addFlashAttribute("success", "Instrument configuration updated successfully!");
        return "redirect:/admin/project/" + projectId + "/instrumentCount/edit";
    }

}
