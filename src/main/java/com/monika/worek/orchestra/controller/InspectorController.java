package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class InspectorController {

    private final ProjectService projectService;

    public InspectorController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/inspectorPage")
    public String showInspectorPage(Model model) {
        List<Project> ongoingProjects = projectService.getOngoingProjects();
        List<Project> futureProjects = projectService.getFutureProjects();

        model.addAttribute("ongoingProjects", ongoingProjects);
        model.addAttribute("futureProjects", futureProjects);
        return "inspectorPage";
    }
}
