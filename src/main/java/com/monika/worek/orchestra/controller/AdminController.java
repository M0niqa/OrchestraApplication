package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final ProjectService projectService;

    public AdminController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/adminPage")
    public String showAdminPage(Model model) {
        model.addAttribute("ongoingProjects", projectService.getOngoingProjects());
        model.addAttribute("futureProjects", projectService.getFutureProjects());
        return "/admin/admin-main-page";
    }

    @GetMapping("/archivedProjects")
    public String showArchivedProjects(Model model) {
        model.addAttribute("archivedProjects", projectService.getArchivedProjects());
        return "archived-projects";
    }
}
