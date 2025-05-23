package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.ProjectService;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminPageController {

    private final ProjectService projectService;
    private final UserService userService;
    private final ChatService chatService;

    public AdminPageController(ProjectService projectService, UserService userService, ChatService chatService) {
        this.projectService = projectService;
        this.userService = userService;
        this.chatService = chatService;
    }

    @GetMapping("/adminPage")
    public String showAdminPage(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        UserBasicDTO userBasicDTO = userService.getUserBasicDtoByEmail(currentEmail);
        long userId = userBasicDTO.getId();
        Boolean unreads = chatService.areUnreadMessages(userBasicDTO.getId());
        model.addAttribute("unreads", unreads);
        model.addAttribute("userId", userId);
        model.addAttribute("ongoingProjects", projectService.getOngoingProjects());
        model.addAttribute("futureProjects", projectService.getFutureProjects());
        return "/admin/admin-main-page";
    }

    @GetMapping("/archived")
    public String showArchivedProjects(Model model) {
        model.addAttribute("archivedProjects", projectService.getArchivedProjects());
        return "archived-projects";
    }

    @GetMapping("/archived/project/{projectId}")
    public String viewArchivedProjectDetails(@PathVariable Long projectId, Model model) {
        ProjectDTO projectDTO = projectService.getProjectDtoById(projectId);
        model.addAttribute("project", projectDTO);
        return "archived-project-details";
    }
}
