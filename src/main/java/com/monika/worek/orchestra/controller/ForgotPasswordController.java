package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.PasswordResetDTO;
import com.monika.worek.orchestra.service.PasswordResetService;
import com.monika.worek.orchestra.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        model.addAttribute("passwordForm", new PasswordResetDTO());
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute("passwordForm") PasswordResetDTO form,
                                BindingResult bindingResult,
                                Model model) {

        model.addAttribute("token", form.getToken());

        if (bindingResult.hasErrors()) {
            return "reset-password";
        }

        String email = passwordResetService.getEmailForToken(form.getToken());
        if (email == null) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", "Passwords do not match.");
            return "reset-password";
        }

        userService.updatePassword(email, form.getNewPassword());
        passwordResetService.invalidateToken(form.getToken());

        return "redirect:/login-form?resetSuccess";
    }

}

