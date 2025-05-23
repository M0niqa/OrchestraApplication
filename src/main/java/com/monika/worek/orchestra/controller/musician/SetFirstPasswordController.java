package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.dto.PasswordResetDTO;
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
public class SetFirstPasswordController {

    private final TokenService tokenService;
    private final UserService userService;

    public SetFirstPasswordController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping("/set-password")
    public String setPasswordPage(@RequestParam String token, Model model) {
        if (tokenService.getEmailForToken(token) == null) {
            return "token-invalid";
        }

        PasswordResetDTO form = new PasswordResetDTO();
        form.setToken(token);
        model.addAttribute("passwordForm", form);
        return "set-password";
    }

    @PostMapping("/set-password")
    public String setPassword(@Valid @ModelAttribute("passwordForm") PasswordResetDTO form, BindingResult bindingResult, Model model) {
        model.addAttribute("token", form.getToken());

        if (bindingResult.hasErrors()) {
            return "set-password";
        }

        String email = tokenService.getEmailForToken(form.getToken());
        if (email == null) {
            return "token-invalid";
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", "Passwords do not match.");
            return "set-password";
        }

        userService.updatePassword(email, form.getNewPassword());
        tokenService.invalidateToken(form.getToken());
        return "redirect:/login?setSuccess";
    }
}
