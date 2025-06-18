package com.monika.worek.orchestra.controller.common;

import com.monika.worek.orchestra.dto.PasswordResetDTO;
import com.monika.worek.orchestra.service.PasswordResetService;
import com.monika.worek.orchestra.service.TokenService;
import com.monika.worek.orchestra.service.UserService;
import jakarta.validation.Valid;
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
    private final TokenService tokenService;

    public ForgotPasswordController(PasswordResetService passwordResetService, UserService userService, TokenService tokenService) {
        this.passwordResetService = passwordResetService;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        passwordResetService.sendResetLink(email);
        return "redirect:/forgot-password?sendSuccess";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        if (tokenService.getEmailForToken(token) == null) {
            return "common/token-invalid";
        }

        PasswordResetDTO form = new PasswordResetDTO();
        form.setToken(token);
        model.addAttribute("passwordForm", form);
        return "common/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute("passwordForm") PasswordResetDTO form, BindingResult bindingResult, Model model) {
        model.addAttribute("token", form.getToken());

        if (bindingResult.hasErrors()) {
            return "common/reset-password";
        }

        String email = tokenService.getEmailForToken(form.getToken());
        if (email == null) {
            return "common/token-invalid";
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", "Passwords do not match.");
            return "common/reset-password";
        }

        userService.updatePassword(email, form.getNewPassword());
        tokenService.invalidateToken(form.getToken());
        return "redirect:/login?resetSuccess";
    }
}

