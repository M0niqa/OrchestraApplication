package com.monika.worek.orchestra.controller.inspector;

import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InspectorProjectController.class)
@WithMockUser(username = "inspector@example.com", roles = "INSPECTOR")
class InspectorProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = Project.builder().id(1L).name("Test Project").build();
    }

    @Test
    void showInviteMusiciansPage_whenGetRequest_thenShouldReturnViewWithModel() throws Exception {
        // given
        when(projectService.getProjectById(1L)).thenReturn(project);
        when(projectService.getRemainingInstrumentsCount(any())).thenReturn(Collections.emptyMap());
        when(projectService.getAvailableMusiciansByInstrument(1L)).thenReturn(new LinkedHashMap<>());

        // when
        // then
        mockMvc.perform(get("/inspector/project/{projectId}/sendInvitation", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("inspector/send-invitation"))
                .andExpect(model().attributeExists("projectId", "projectName", "remainingCounts", "musiciansByInstrument", "musicianIds"));
    }

    @Test
    void inviteMusicians_whenDataIsValid_thenShouldInviteAndRedirects() throws Exception {
        // given
        Long projectId = 1L;
        LocalDateTime deadline = LocalDateTime.now().plusDays(5);

        doNothing().when(projectService).inviteMusician(anyLong(), anyLong(), any(LocalDateTime.class));

        // when
        // then
        mockMvc.perform(post("/inspector/project/{projectId}/sendInvitation", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("musicianIds", "10", "11")
                        .param("invitationDeadline", deadline.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inspector/project/" + projectId + "/sendInvitation"))
                .andExpect(flash().attribute("success", "Invitations sent successfully!"));

        verify(projectService, times(2)).inviteMusician(eq(projectId), anyLong(), eq(deadline));
    }

    @Test
    void inviteMusicians_whenDeadlineIsMissing_thenShouldReturnFormWithError() throws Exception {
        // given
        Long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(project);

        // when
        // then
        mockMvc.perform(post("/inspector/project/{projectId}/sendInvitation", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("musicianIds", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("inspector/send-invitation"))
                .andExpect(model().attributeExists("deadlineError"));

        verify(projectService, never()).inviteMusician(anyLong(), anyLong(), any());
    }

    @Test
    void showMusicianStatus_whenGetRequest_thenShouldReturnViewWithStatus() throws Exception {
        // given
        Long projectId = 1L;
        ProjectDTO projectDTO = ProjectDTO.builder()
                .musiciansWhoRejected(new HashSet<>())
                .invited(new HashSet<>())
                .build();
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectService.getProjectDtoById(projectId)).thenReturn(projectDTO);
        when(projectService.getProjectMembersByInstrument(project)).thenReturn(new LinkedHashMap<>());

        // when
        // then
        mockMvc.perform(get("/inspector/project/{projectId}/musicianStatus", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("inspector/musician-status"))
                .andExpect(model().attributeExists("projectMembersByInstrument", "musiciansWhoRejected", "pendingMusicians", "project"));
    }

    @Test
    void removeMusicianFromProject_whenPostRequest_thenShouldRemoveAndRedirects() throws Exception {
        // given
        Long projectId = 1L;
        Long musicianId = 10L;
        doNothing().when(projectService).removeProjectMember(projectId, musicianId);

        // when
        // then
        mockMvc.perform(post("/inspector/project/{projectId}/remove/{musicianId}", projectId, musicianId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inspector/project/" + projectId + "/musicianStatus"))
                .andExpect(flash().attribute("success", "Musician removed successfully!"));

        verify(projectService, times(1)).removeProjectMember(projectId, musicianId);
    }
}