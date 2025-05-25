package com.monika.worek.orchestra.controller.inspector;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class InspectorPageController {

    private final ProjectService projectService;

    public InspectorPageController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/inspectorPage")
    public String showInspectorPage(Model model) {
        List<ProjectBasicInfoDTO> ongoingProjects = projectService.getOngoingProjectsDTOs();
        List<ProjectBasicInfoDTO> futureProjects = projectService.getFutureProjectsDTOs();

        model.addAttribute("ongoingProjects", ongoingProjects);
        model.addAttribute("futureProjects", futureProjects);
        return "/inspector/inspector-main-page";
    }

    @GetMapping({"admin/archived", "inspector/archived"})
    public String showArchivedProjects(Model model) {
        model.addAttribute("archivedProjects", projectService.getArchivedProjectsDTOs());
        return "inspector/archived-projects";
    }

    @GetMapping({"admin/archived/project/{projectId}", "inspector/archived/project/{projectId}"})
    public String viewArchivedProjectDetails(@PathVariable Long projectId, Model model) {
        ProjectDTO projectDTO = projectService.getProjectDtoById(projectId);
        model.addAttribute("project", projectDTO);
        return "inspector/archived-project-details";
    }
}
