package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.MusicianDTO;
import com.monika.worek.orchestra.model.TaxOffice;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.MusicianService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
public class MusicianController {

    private final MusicianService musicianService;
    private final ChatService chatService;

    public MusicianController(MusicianService musicianService, ChatService chatService) {
        this.musicianService = musicianService;
        this.chatService = chatService;
    }

    @GetMapping("/musicianPage")
    public String showMusicianPage(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        MusicianBasicDTO musicianBasicDTO = musicianService.getMusicianBasicDtoByEmail(currentEmail);
        model.addAttribute("userId", musicianBasicDTO.getId());

        Boolean unreads = chatService.areUnreadMessages(musicianBasicDTO.getId());
        model.addAttribute("unreads", unreads);
        model.addAttribute("musician", musicianBasicDTO);
        return "musician/musician-home-page";
    }

    @GetMapping("/musicianData")
    public String showUpdateDataForm(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        model.addAttribute("musician", musicianService.getMusicianDtoByEmail(currentEmail));
        model.addAttribute("taxOffices", TaxOffice.values());
        return "musician/musician-data";
    }

    @PostMapping("/musicianData")
    public String updateData(@Valid @ModelAttribute("musician") MusicianDTO dto, BindingResult bindingResult,
                             Authentication authentication, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("taxOffices", TaxOffice.values());
            return "musician/musician-data";
        }

        musicianService.updateUserData(authentication.getName(), dto);
        redirectAttributes.addFlashAttribute("success", "Data updated successfully!");
        return "redirect:/musicianData";
    }
}
