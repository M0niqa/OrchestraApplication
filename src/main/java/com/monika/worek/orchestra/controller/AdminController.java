package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.service.ProjectService;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final ProjectService projectService;
    private final UserService userService;

    public AdminController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
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

    @GetMapping("/admin/allUsers")
    public String showAllUsers(Model model) {
        List<UserBasicDTO> usersDTO = userService.getAllBasicDTOUsers();
        model.addAttribute("allUsers", usersDTO);
        return "/admin/admin-all-users";
    }

//    @PostMapping("/admin/allUsers/{userId}")
//    public String deleteMusician(@PathVariable Long userId) {
//        userService.deleteUserById(userId);
//        return "redirect:/admin/allUsers";
//    }
}
