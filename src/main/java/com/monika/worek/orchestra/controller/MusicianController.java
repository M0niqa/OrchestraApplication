package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.dto.MusicianDTO;
import com.monika.worek.orchestra.model.TaxOffice;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MusicianController {

    private final MusicianService musicianService;

    public MusicianController(MusicianService musicianService) {
        this.musicianService = musicianService;
    }

    @GetMapping("/musicianPage")
    public String showUserPage(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        MusicianDTO userDTO = musicianService.findMusicianByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("musician", userDTO);
        return "musician";
    }

    @GetMapping("/userData")
    public String showUpdateDataForm(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        MusicianDTO musicianDTO = musicianService.findMusicianByEmail(currentEmail).orElseThrow();
        model.addAttribute("musician", musicianDTO);
        model.addAttribute("instruments", Instrument.values());
        model.addAttribute("taxOffices", TaxOffice.values());
        return "musicianData";
    }

    @PostMapping("/userData")
    public String updateData(@Valid @ModelAttribute("musician") MusicianDTO dto,
                             BindingResult bindingResult,
                             Authentication authentication,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("instruments", Instrument.values());
            model.addAttribute("taxOffices", TaxOffice.values());
            return "musicianData";
        }
        musicianService.updateUserData(authentication.getName(), dto);
        return "redirect:/musicianPage";
    }

}
