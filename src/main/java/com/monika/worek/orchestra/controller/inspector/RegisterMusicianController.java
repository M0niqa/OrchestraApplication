package com.monika.worek.orchestra.controller.inspector;

import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterMusicianController {

    private final RegistrationService registrationService;

    public RegisterMusicianController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping({"/inspector/registerMusician", "/admin/registerMusician"})
    public String showRegisterMusician(Model model) {
        model.addAttribute("musician", MusicianRegisterDTO.builder().build());
        model.addAttribute("instruments", Instrument.values());
        return "/inspector/registration-form";
    }

    @PostMapping({"/inspector/registerMusician", "/admin/registerMusician"})
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
}
