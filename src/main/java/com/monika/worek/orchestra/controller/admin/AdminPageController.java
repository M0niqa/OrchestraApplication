package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.ProjectService;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        model.addAttribute("ongoingProjects", projectService.getOngoingProjectsDTOs());
        model.addAttribute("futureProjects", projectService.getFutureProjectsDTOs());
        return "/admin/admin-main-page";
    }
}













