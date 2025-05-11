package com.monika.worek.orchestra.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    private final ProjectService projectService;
    private final UserService userService;
    private final ChatService chatService;

    public AdminController(ProjectService projectService, UserService userService, ChatService chatService) {
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

    @GetMapping("/admin/allUsers")
    public String showAllUsers(Model model) {
        List<UserBasicDTO> usersDTO = userService.getAllBasicDTOUsers();
        model.addAttribute("allUsers", usersDTO);
        return "admin/admin-all-users";
    }

    @PostMapping("/admin/allUsers/{userId}")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(userId);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/admin/allUsers";
    }
}
