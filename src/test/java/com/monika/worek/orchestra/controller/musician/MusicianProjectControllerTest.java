package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.calculator.WageCalculator;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MusicianProjectController.class)
@WithMockUser(username = "musician@example.com", roles = "MUSICIAN")
class MusicianProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private MusicianService musicianService;

    private Musician musician;
    private Project project;
    private final String userEmail = "musician@example.com";
    private final Long projectId = 1L;

    @BeforeEach
    void setUp() {
        musician = Musician.builder().id(1L).email(userEmail).build();
        project = Project.builder().id(projectId).projectMembers(new HashSet<>()).build();
    }

    @Test
    void viewProjectDetails_whenAuthorized_thenShouldReturnDetailsViewWithModel() throws Exception {
        // given
        BigDecimal wage = new BigDecimal("1000.00");
        when(musicianService.getMusicianByEmail(userEmail)).thenReturn(musician);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(musicianService.isDataMissing(musician)).thenReturn(false);

        try (MockedStatic<WageCalculator> utilities = Mockito.mockStatic(WageCalculator.class)) {
            utilities.when(() -> WageCalculator.getGrossWage(project, musician))
                    .thenReturn(wage);

            doNothing().when(projectService).throwIfUnauthorized(projectId, userEmail);

            // when
            // then
            mockMvc.perform(get("/musician/project/{projectId}", projectId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/musician/musician-project-details"))
                    .andExpect(model().attributeExists("isDataMissing", "project", "accepted"))
                    .andExpect(model().attribute("wage", wage))
                    .andExpect(model().attribute("isDataMissing", false));
        }
    }

    @Test
    void viewProjectDetails_whenNotAuthorized_thenShouldReturnForbiddenStatus() throws Exception {
        // given
        doThrow(new AccessDeniedException("Access Denied")).when(projectService).throwIfUnauthorized(projectId, userEmail);

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}", projectId))
                .andExpect(status().isForbidden());
    }

    @Test
    void acceptInvitation_whenAuthorized_thenShouldAcceptAndRedirect() throws Exception {
        // given
        when(musicianService.getMusicianByEmail(userEmail)).thenReturn(musician);
        doNothing().when(projectService).throwIfUnauthorized(projectId, userEmail);
        doNothing().when(projectService).acceptInvitation(projectId, musician.getId());

        // when
        // then
        mockMvc.perform(post("/musician/project/{projectId}/accept", projectId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/musician/project/" + projectId));

        verify(projectService, times(1)).acceptInvitation(projectId, musician.getId());
    }

    @Test
    void rejectInvitation_whenAuthorized_thenShouldRejectAndRedirect() throws Exception {
        // given
        when(musicianService.getMusicianByEmail(userEmail)).thenReturn(musician);
        doNothing().when(projectService).throwIfUnauthorized(projectId, userEmail);
        doNothing().when(projectService).rejectInvitation(projectId, musician.getId());

        // when
        // then
        mockMvc.perform(post("/musician/project/{projectId}/reject", projectId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/musicianPage"));

        verify(projectService, times(1)).rejectInvitation(projectId, musician.getId());
    }
}