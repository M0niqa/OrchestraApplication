package com.monika.worek.orchestra.controller.inspector;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Controller
public class InspectorProjectController {

    private final ProjectService projectService;

    public InspectorProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/inspector/project/{projectId}/sendInvitation")
    public String showInviteMusiciansPage(@PathVariable Long projectId, Model model) {
        projectService.updatePendingInvitations(projectId);
        prepareSendInvitationModel(projectId, model);
        model.addAttribute("musicianIds", new ArrayList<Long>());

        return "inspector/send-invitation";
    }

    @PostMapping("/inspector/project/{projectId}/sendInvitation")
    public String inviteMusicians(@PathVariable Long projectId, @RequestParam(value = "musicianIds", required = false) List<Long> musicianIds, @RequestParam(value = "invitationDeadline", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime invitationDeadline, RedirectAttributes redirectAttributes, Model model) {
        if (invitationDeadline == null) {
            model.addAttribute("deadlineError", "Please set an invitation deadline.");
            model.addAttribute("musicianIds", musicianIds);
            prepareSendInvitationModel(projectId, model);
            return "inspector/send-invitation";
        }
        if (musicianIds != null && !musicianIds.isEmpty()) {
            for (Long musicianId : musicianIds) {
                projectService.inviteMusician(projectId, musicianId, invitationDeadline);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Invitations sent successfully!");
        return "redirect:/inspector/project/" + projectId + "/sendInvitation";
    }

    private void prepareSendInvitationModel(Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("projectName", project.getName());
        model.addAttribute("remainingCounts", projectService.getRemainingInstrumentsCount(project));
        model.addAttribute("musiciansByInstrument", projectService.getAvailableMusiciansByInstrument(projectId));
    }

    @GetMapping({"/admin/project/{projectId}/musicianStatus", "/inspector/project/{projectId}/musicianStatus"})
    public String showMusicianStatus(@PathVariable Long projectId, Model model) {
        projectService.updatePendingInvitations(projectId);
        Project project = projectService.getProjectById(projectId);
        ProjectDTO projectDTO = projectService.getProjectDtoById(projectId);
        LinkedHashMap<Instrument, List<MusicianBasicDTO>> musiciansByInstrument = projectService.getProjectMembersByInstrument(project);
        model.addAttribute("projectMembersByInstrument", musiciansByInstrument);
        model.addAttribute("musiciansWhoRejected", projectDTO.getMusiciansWhoRejected());
        model.addAttribute("pendingMusicians", projectDTO.getInvited());
        model.addAttribute("project", projectDTO);

        return "inspector/musician-status";
    }

    @PostMapping("/inspector/project/{projectId}/remove/{musicianId}")
    public String removeMusicianFromProject(@PathVariable Long projectId, @PathVariable Long musicianId, RedirectAttributes redirectAttributes) {
        projectService.removeProjectMember(projectId, musicianId);
        redirectAttributes.addFlashAttribute("success", "Musician removed successfully!");
        return "redirect:/inspector/project/{projectId}/musicianStatus";
    }
}
