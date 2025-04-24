package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.UserDTO;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/adminPage")
    public String showUserPage(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        UserDTO userDTO = userService.findUserByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("user", userDTO);
        return "admin";
    }
}
