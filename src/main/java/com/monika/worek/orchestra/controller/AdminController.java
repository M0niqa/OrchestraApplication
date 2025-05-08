package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.auth.MusicianBasicDTOMapper;
import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AdminController {

    private final ProjectService projectService;
    private final MusicianService musicianService;

    public AdminController(ProjectService projectService, MusicianService musicianService) {
        this.projectService = projectService;
        this.musicianService = musicianService;
    }

    @GetMapping("/adminPage")
    public String showAdminPage(Model model) {
        model.addAttribute("ongoingProjects", projectService.getOngoingProjects());
        model.addAttribute("futureProjects", projectService.getFutureProjects());
        return "/admin/admin-main-page";
    }

    @GetMapping("/archivedProjects")
    public String showArchivedProjects(Model model) {
        model.addAttribute("archivedProjects", projectService.getArchivedProjects());
        return "archived-projects";
    }

    @GetMapping("/admin/allMusicians")
    public String showAllMusicians(Model model) {
        List<MusicianBasicDTO> musiciansDTO = musicianService.getAllMusiciansSortedBySurname().stream().map(MusicianBasicDTOMapper::mapToDto).toList();
        model.addAttribute("allMusicians", musiciansDTO);
        return "/admin/admin-all-musicians";
    }

    @PostMapping("/admin/allMusicians/{musicianId}")
    public String deleteMusician(@PathVariable Long musicianId) {
        musicianService.deleteMusicianById(musicianId);
        return "redirect:/admin/allMusicians";
    }
}
