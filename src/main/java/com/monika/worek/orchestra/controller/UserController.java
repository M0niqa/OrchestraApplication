package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.PasswordUpdateDTO;
import com.monika.worek.orchestra.dto.UserDTO;
import com.monika.worek.orchestra.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userPassword")
    public String showUpdatePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordUpdateDTO());
        return "update-password";
    }

    @PostMapping("/userPassword")
    public String updatePassword(@Valid @ModelAttribute("passwordForm") PasswordUpdateDTO form, BindingResult bindingResult,
                                 Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            return "update-password";
        }

        String currentEmail = authentication.getName();
        UserDTO user = userService.findUserByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String storedPassword = user.getPassword();

        if (!passwordEncoder.matches(form.getOldPassword(), storedPassword)) {
            model.addAttribute("error", "Incorrect old password.");
            return "update-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            model.addAttribute("error", "New passwords do not match.");
            return "update-password";
        }

        userService.updatePassword(currentEmail, form.getNewPassword());
        model.addAttribute("success", "Password changed successfully!");
        return "update-password";
    }
}
