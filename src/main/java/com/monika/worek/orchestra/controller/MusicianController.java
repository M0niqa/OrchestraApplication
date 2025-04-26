package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.MusicianDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.TaxOffice;
import com.monika.worek.orchestra.service.MusicianService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MusicianController {

    private final MusicianService musicianService;

    public MusicianController(MusicianService musicianService) {
        this.musicianService = musicianService;
    }

    @GetMapping("/musicianPage")
    public String showUserPage(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        MusicianDTO musicianDTO = musicianService.findMusicianByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("musician", musicianDTO);
        return "musicianPage";
    }

    @GetMapping("/userData")
    public String showUpdateDataForm(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        MusicianDTO musicianDTO = musicianService.findMusicianByEmail(currentEmail).orElseThrow();
        model.addAttribute("musician", musicianDTO);
        model.addAttribute("instruments", Instrument.values());
        model.addAttribute("taxOffices", TaxOffice.values());
        return "musician-data";
    }

    @PostMapping("/userData")
    public String updateData(@Valid @ModelAttribute("musician") MusicianDTO dto, BindingResult bindingResult,
                             Authentication authentication, Model model) {
        model.addAttribute("instruments", Instrument.values());
        model.addAttribute("taxOffices", TaxOffice.values());

        if (bindingResult.hasErrors()) {
            return "musician-data";
        }

        musicianService.updateUserData(authentication.getName(), dto);
        model.addAttribute("success", "Data updated successfully!");
        return "musician-data";
    }

}
