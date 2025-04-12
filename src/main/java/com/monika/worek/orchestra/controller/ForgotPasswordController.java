package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.service.PasswordResetService;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    private final PasswordResetService passwordResetService;
    private final UserService userService;

    public ForgotPasswordController(PasswordResetService passwordResetService, UserService userService) {
        this.passwordResetService = passwordResetService;
        this.userService = userService;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        passwordResetService.sendResetLink(email);
        return ResponseEntity.ok("If your email exists in our system, a reset link has been sent.");
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        if (passwordResetService.getEmailForToken(token) == null) {
            return "token-invalid";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                @RequestParam String confirmNewPassword,
                                Model model) {

        String email = passwordResetService.getEmailForToken(token);
        if (email == null) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset-password";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        userService.updatePassword(email, newPassword);
        passwordResetService.invalidateToken(token);

        return "redirect:/login?resetSuccess";
    }
}

