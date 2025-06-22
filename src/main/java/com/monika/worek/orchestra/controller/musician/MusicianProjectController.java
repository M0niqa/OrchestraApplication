package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.mappers.ProjectDTOMapper;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

import static com.monika.worek.orchestra.calculator.WageCalculator.getGrossWage;

@Controller
public class MusicianProjectController {

    private final ProjectService projectService;
    private final MusicianService musicianService;

    public MusicianProjectController(ProjectService projectService, MusicianService musicianService) {
        this.projectService = projectService;
        this.musicianService = musicianService;
    }

    @GetMapping("/musician/project/{projectId}")
    public String viewProjectDetails(@PathVariable Long projectId, Model model, Authentication authentication) {
        String email = authentication.getName();

        projectService.throwIfUnauthorized(projectId, email);

        Musician musician = musicianService.getMusicianByEmail(email);
        Project project = projectService.getProjectById(projectId);
        Boolean isDataMissing = musicianService.isDataMissing(musician);
        ProjectDTO projectDTO = ProjectDTOMapper.mapToDto(project);
        boolean accepted = project.getProjectMembers().contains(musician);
        BigDecimal wage = getGrossWage(project, musician);

        model.addAttribute("isDataMissing", isDataMissing);
        model.addAttribute("project", projectDTO);
        model.addAttribute("accepted", accepted);
        model.addAttribute("wage", wage);
        return "/musician/musician-project-details";
    }

    @PostMapping("/musician/project/{projectId}/accept")
    public String acceptInvitation(@PathVariable Long projectId, Authentication authentication) {
        String email = authentication.getName();
        projectService.throwIfUnauthorized(projectId, email);

        Musician musician = musicianService.getMusicianByEmail(email);
        projectService.acceptInvitation(projectId, musician.getId());
        return "redirect:/musician/project/" + projectId;
    }

    @PostMapping("/musician/project/{projectId}/reject")
    public String rejectInvitation(@PathVariable Long projectId, Authentication authentication) {
        String email = authentication.getName();
        projectService.throwIfUnauthorized(projectId, email);

        Musician musician = musicianService.getMusicianByEmail(email);
        projectService.rejectInvitation(projectId, musician.getId());
        return "redirect:/musicianPage";
    }
}
