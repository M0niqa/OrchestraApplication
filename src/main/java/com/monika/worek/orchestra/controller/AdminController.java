package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final ProjectService projectService;

    public AdminController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/adminPage")
    public String showAdminPage(Model model) {
        List<Project> ongoingProjects = projectService.getOngoingProjects();
        List<Project> futureProjects = projectService.getFutureProjects();

        model.addAttribute("ongoingProjects", ongoingProjects);
        model.addAttribute("futureProjects", futureProjects);
        return "admin";
    }

    @GetMapping("/archivedProjects")
    public String showArchivedProjects(Model model) {
        model.addAttribute("archivedProjects", projectService.getArchivedProjects());
        return "archived-projects";
    }
}
