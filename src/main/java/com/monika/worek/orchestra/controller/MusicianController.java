package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.TaxOffice;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MusicianController {

    private final MusicianService musicianService;
    private final ChatService chatService;
    private final UserService userService;

    public MusicianController(MusicianService musicianService, ChatService chatService, UserService userService) {
        this.musicianService = musicianService;
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping("/musicianPage")
    public String showMusicianPage(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        MusicianBasicDTO musicianBasicDTO = musicianService.getMusicianBasicDtoByEmail(currentEmail);
        long musicianId = musicianBasicDTO.id();
        model.addAttribute("musicianId", musicianId);

        Boolean unreads = chatService.areUnreadMessages(musicianBasicDTO.id());
        model.addAttribute("unreads", unreads);
        model.addAttribute("musician", musicianBasicDTO);
        model.addAttribute("pendingProjects", musicianService.getActivePendingProjects(musicianId));
        model.addAttribute("acceptedProjects", musicianService.getActiveAcceptedProjects(musicianId));
        model.addAttribute("rejectedProjects", musicianService.getActiveRejectedProjects(musicianId));
        return "musician/musician-home-page";
    }

    @GetMapping("/musician/{musicianId}/archivedProjects")
    public String showArchivedProjects(@PathVariable Long musicianId, Model model) {
        model.addAttribute("archivedAcceptedProjects", musicianService.getArchivedAcceptedProjects(musicianId));
        model.addAttribute("archivedRejectedProjects", musicianService.getArchivedRejectedProjects(musicianId));
        return "musician/musician-archived-projects";
    }

    @GetMapping("/musicianData")
    public String showUpdateDataForm(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        model.addAttribute("musician", musicianService.getMusicianDtoByEmail(currentEmail));
        model.addAttribute("taxOffices", TaxOffice.values());
        return "musician/musician-data";
    }

    @PostMapping("/musicianData")
    public String updateData(@Valid @ModelAttribute("musician") MusicianDataDTO dto, BindingResult bindingResult,
                             Authentication authentication, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("taxOffices", TaxOffice.values());
            return "musician/musician-data";
        }

        String currentEmail = authentication.getName();
        Musician musician = musicianService.getMusicianByEmail(currentEmail);

        boolean emailChanged = !musician.getEmail().equals(dto.getEmail());
        if (emailChanged) {
            if (userService.doesUserExist(dto.getEmail())) {
                bindingResult.rejectValue("email", "error.email", "User with this email already exists.");
                model.addAttribute("taxOffices", TaxOffice.values());
                return "musician/musician-data";
            }
            musicianService.updateMusicianData(authentication.getName(), dto);
            SecurityContextHolder.clearContext();
            return "redirect:/login?emailChanged";
        }

        musicianService.updateMusicianData(authentication.getName(), dto);
        redirectAttributes.addFlashAttribute("success", "Data updated successfully!");
        return "redirect:/musicianPage";
    }
}
