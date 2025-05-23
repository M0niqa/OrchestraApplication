package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.MusicianService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MusicianPageController {

    private final MusicianService musicianService;
    private final ChatService chatService;

    public MusicianPageController(MusicianService musicianService, ChatService chatService) {
        this.musicianService = musicianService;
        this.chatService = chatService;
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
}
