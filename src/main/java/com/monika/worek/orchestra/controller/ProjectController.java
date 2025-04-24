package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.auth.ProjectDTOMapper;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import com.monika.worek.orchestra.service.AgreementGenerationService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final MusicianRepository musicianRepository;
    private final AgreementGenerationService agreementGenerationService;

    public ProjectController(ProjectRepository projectRepository, ProjectService projectService, MusicianRepository musicianRepository, AgreementGenerationService agreementGenerationService) {
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.musicianRepository = musicianRepository;
        this.agreementGenerationService = agreementGenerationService;
    }

    @GetMapping("/addProject")
    public String showAddProjectForm(Model model) {
        model.addAttribute("projectDTO", new ProjectDTO());
        return "addProject";
    }

    @PostMapping("/addProject")
    public String addProject(@ModelAttribute ProjectDTO projectDTO) {
        Project project = ProjectDTOMapper.mapToEntity(projectDTO);
        projectRepository.save(project);
        return "redirect:/";
    }

    @GetMapping("/projects")
    public String showAllActiveProjects(Model model) {
        List<ProjectDTO> activeProjects = projectService.getAllActiveProjects()
                .stream()
                .map(ProjectDTOMapper::mapToDTO)
                .toList();

        model.addAttribute("activeProjects", activeProjects);
        return "activeProjects";
    }

    @GetMapping("/project/{projectId}/inviteMusicians")
    public String showAddMusiciansPage(@PathVariable Long projectId, Model model) {
        List<Musician> availableMusicians = projectService.getAvailableMusicians(projectId);
        Map<Instrument, List<Musician>> musiciansByInstrument = availableMusicians.stream()
                .collect(Collectors.groupingBy(Musician::getInstrument));

        model.addAttribute("musiciansByInstrument", musiciansByInstrument);
        model.addAttribute("projectId", projectId);
        return "sendInvitation";
    }

    @PostMapping("/project/{projectId}/inviteMusicians")
    public String inviteMusicians(@PathVariable Long projectId, @RequestParam(value = "musicianIds", required = false) List<Long> musicianIds) {
        if (musicianIds != null && !musicianIds.isEmpty()) {
            for (Long musicianId : musicianIds) {
                projectService.inviteMusician(projectId, musicianId);
            }
        }
        return "redirect:/project/" + projectId;
    }

    @GetMapping("/project/{projectId}/musicianStatus")
    public String showMusicianStatus(@PathVariable Long projectId, Model model) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        model.addAttribute("projectMembers", project.getProjectMembers());
        model.addAttribute("refusedMusicians", project.getMusiciansWhoRefused());
        model.addAttribute("pendingMusicians", project.getInvited());
        model.addAttribute("project", project);

        return "musicianStatus";
    }

    @GetMapping("/project/{projectId}/invitation/view")
    public String viewInvitation(@PathVariable Long projectId, Model model, Authentication authentication) throws AccessDeniedException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Musician musician = musicianRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Musician not found"));

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (!project.getInvited().contains(musician)) {
            throw new AccessDeniedException("You are not invited to this project.");
        }

        String agreementContent = agreementGenerationService.generateAgreementContent(project, musician);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDTO(project);

        model.addAttribute("project", projectDTO);
        model.addAttribute("agreementContent", agreementContent);

        return "view-invitation";
    }

    // Endpoint for musician to accept
    @PostMapping("/project/{projectId}/invitation/accept")
    public String acceptInvitation(@PathVariable Long projectId, Authentication authentication) {
        // Get logged-in musician (same as above)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Musician musician = musicianRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));

        try {
            projectService.acceptInvitation(projectId, musician.getId());
            // Redirect to a confirmation page or the musician's dashboard/project list
            return "redirect:/musician/dashboard?acceptSuccess";
        } catch (Exception e) {
            // Log error
            // Redirect to an error page or back to invitation view with error message
            return "redirect:/project/" + projectId + "/invitation/view?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    // Endpoint for musician to reject
    @PostMapping("/project/{projectId}/invitation/reject")
    public String rejectInvitation(@PathVariable Long projectId, Authentication authentication) {
        // Get logged-in musician (same as above)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Musician musician = musicianRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));

        try {
            projectService.rejectInvitation(projectId, musician.getId());
            // Redirect to a confirmation page or the musician's dashboard/project list
            return "redirect:/musician/dashboard?rejectSuccess";
        } catch (Exception e) {
            // Log error
            // Redirect to an error page or back to invitation view with error message
            return "redirect:/project/" + projectId + "/invitation/view?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
