package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.dto.MusicianDTO;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.PasswordResetService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MusicianController {

    private final MusicianService musicianService;
    private final PasswordResetService passwordResetService;

    public MusicianController(MusicianService musicianService, PasswordResetService passwordResetService) {
        this.musicianService = musicianService;
        this.passwordResetService = passwordResetService;
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
        MusicianDTO userDTO = musicianService.findMusicianByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("musician", userDTO);
        model.addAttribute("instruments", Instrument.values());
        return "musicianData";
    }

    @PostMapping("/userData")
    public String updateData(Authentication authentication, @ModelAttribute MusicianDTO musicianDTO) {
        String currentEmail = authentication.getName();
        musicianService.updateUserData(currentEmail, musicianDTO);
        return "redirect:/musicianPage";
    }

    @GetMapping("/registerMusician")
    public String showRegisterMusician() {
        return "registration-form";
    }

    @PostMapping("/registerMusician")
    public String registerMusician(@ModelAttribute MusicianRegisterDTO musicianRegisterDTO) {
        musicianService.createMusician(musicianRegisterDTO);
        passwordResetService.sendResetLink(musicianRegisterDTO.getEmail());
        return "redirect:/registration-confirm";
    }

    @GetMapping("/registration-confirm")
    public String showConfirmationPage() {
        return "registration-confirm";
    }

}
