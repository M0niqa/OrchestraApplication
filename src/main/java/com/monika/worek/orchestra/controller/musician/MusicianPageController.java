package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class MusicianPageController {

    private final MusicianService musicianService;
    private final ChatService chatService;
    private final ProjectService projectService;

    public MusicianPageController(MusicianService musicianService, ChatService chatService, ProjectService projectService) {
        this.musicianService = musicianService;
        this.chatService = chatService;
        this.projectService = projectService;
    }

    @GetMapping("/musicianPage")
    public String showMusicianPage(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        MusicianBasicDTO musicianBasicDTO = musicianService.getMusicianBasicDtoByEmail(currentEmail);
        long musicianId = musicianBasicDTO.id();
        List<ProjectBasicInfoDTO> futureProjects = projectService.getFutureProjectsDTOsByMusicianId(musicianId);

        model.addAttribute("musicianId", musicianId);

        Boolean unreads = chatService.areUnreadMessages(musicianId );
        model.addAttribute("unreads", unreads);
        model.addAttribute("musician", musicianBasicDTO);
        model.addAttribute("pendingProjects", musicianService.getActivePendingProjectsDTOs(musicianId));
        model.addAttribute("acceptedProjects", musicianService.getActiveAcceptedProjectsDTOs(musicianId));
        model.addAttribute("rejectedProjects", musicianService.getActiveRejectedProjectsDTOs(musicianId));
        model.addAttribute("futureProjects", futureProjects);
        return "musician/musician-home-page";
    }

    @GetMapping("/musician/{musicianId}/archivedProjects")
    public String showArchivedProjects(@PathVariable Long musicianId, Model model) {
        model.addAttribute("archivedAcceptedProjects", musicianService.getArchivedAcceptedProjectsDTOs(musicianId));
        model.addAttribute("archivedRejectedProjects", musicianService.getArchivedRejectedProjectsDTOs(musicianId));
        return "musician/musician-archived-projects";
    }
}
