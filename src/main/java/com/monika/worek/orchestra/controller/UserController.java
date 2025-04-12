package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.UserDTO;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userPassword")
    public String showUpdatePasswordForm() {
        return "updatePassword"; // .html
    }

    @PostMapping("/userPassword")
    public String updatePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            Model model,
            Authentication authentication) {

        String currentEmail = authentication.getName();
        UserDTO user = userService.findUserByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String storedPassword = user.getPassword();

        if (!passwordEncoder.matches(oldPassword, storedPassword)) {
            model.addAttribute("error", "Incorrect old password.");
            model.addAttribute("user", user);
            return "updatePassword";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            model.addAttribute("user", user);
            return "updatePassword"; // .html
        }

        userService.updatePassword(currentEmail, newPassword);
        return "redirect:/musicianPage";
    }

}
