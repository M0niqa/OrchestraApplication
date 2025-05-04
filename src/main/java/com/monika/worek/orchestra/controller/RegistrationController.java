package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
import com.monika.worek.orchestra.dto.PasswordResetDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.service.RegistrationService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;
    private final TokenService tokenService;
    private final UserService userService;

    public RegistrationController(RegistrationService registrationService, TokenService tokenService, UserService userService) {
        this.registrationService = registrationService;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping("/inspector/registerMusician")
    public String showRegisterMusician(Model model) {
        model.addAttribute("musician", MusicianRegisterDTO.builder().build());
        model.addAttribute("instruments", Instrument.values());
        return "/inspector/registration-form";
    }

    @PostMapping("/inspector/registerMusician")
    public String registerMusician(@Valid @ModelAttribute("musician") MusicianRegisterDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("instruments", Instrument.values());
            return "/inspector/registration-form";
        }

        try {
            registrationService.createMusician(dto);
            registrationService.sendLink(dto.getEmail());
            redirectAttributes.addFlashAttribute("success", "Musician registered successfully!");
            return "redirect:/inspector/registerMusician";
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage().contains("Musician already exists") ? "User with this email already exists." : "Registration failed due to an internal error.";
            model.addAttribute("error", errorMessage);
            model.addAttribute("instruments", Instrument.values());
            return "/inspector/registration-form";
        }
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
