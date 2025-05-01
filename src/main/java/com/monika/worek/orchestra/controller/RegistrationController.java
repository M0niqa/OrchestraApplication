package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
import com.monika.worek.orchestra.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/registerMusician")
    public String showRegisterMusician(Model model) {
        model.addAttribute("musician", MusicianRegisterDTO.builder().build());
        return "registration-form";
    }

    @PostMapping("/registerMusician")
    public String registerMusician(@Valid @ModelAttribute("musician") MusicianRegisterDTO dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration-form";
        }
        try {
            registrationService.createMusician(dto);
            registrationService.sendLink(dto.getEmail());
            return "registration-confirm";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage().contains("Musician already exists") ? "UserExists" : "RegistrationFailed");
            return "registration-form";
        }
    }
}
