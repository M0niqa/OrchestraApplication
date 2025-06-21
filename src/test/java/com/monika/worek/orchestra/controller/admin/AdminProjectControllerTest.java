package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.EmailService;
import com.monika.worek.orchestra.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminProjectController.class)
@WithMockUser(username = "admin@example.com", roles = "ADMIN")
class AdminProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private EmailService emailService;

    @Test
    void showAddProjectForm_whenGetRequest_thenShouldReturnFormView() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/admin/addProject"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/add-project"))
                .andExpect(model().attributeExists("projectDTO"));
    }

    @Test
    void addProject_whenDataIsValid_thenShouldSaveProjectAndRedirects() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(post("/admin/addProject")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "New Year's Concert")
                        .param("startDate", "2025-12-31")
                        .param("endDate", "2026-01-01")
                        .param("location", "Vienna")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/addProject"))
                .andExpect(flash().attribute("success", "Project added successfully!"));

        verify(projectService, times(1)).saveProject(any(Project.class));
    }

    @Test
    void addProject_whenDataIsInvalid_thenShouldReturnFormViewWithErrors() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(post("/admin/addProject")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/add-project"))
                .andExpect(model().attributeHasFieldErrors("projectDTO", "name"));

        verify(projectService, never()).saveProject(any());
    }

    @Test
    void deleteProject_whenPostRequest_thenShouldDeleteAndRedirect() throws Exception {
        // given
        Long projectId = 1L;

        // when
        // then
        mockMvc.perform(post("/admin/project/{id}/delete", projectId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adminPage"));

        verify(projectService, times(1)).deleteProjectById(projectId);
    }

    @Test
    void updateProject_whenNotifyMembersIsFalse_thenShouldUpdateProjectAndRedirect() throws Exception {
        // given
        Long projectId = 1L;

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/update", projectId)
                        .param("name", "Updated Name")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-05")
                        .param("location", "Berlin")
                        .param("notifyMembers", "false")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/" + projectId))
                .andExpect(flash().attribute("success", "Project updated successfully!"));

        verify(projectService, times(1)).updateBasicProjectInfo(eq(projectId), any(ProjectBasicInfoDTO.class));
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    void updateProject_whenNotifyMembersIsTrue_thenShouldUpdateAndSendEmails() throws Exception {
        // given
        Long projectId = 1L;
        Musician musician1 = Musician.builder().email("musician1@example.com").build();
        Musician musician2 = Musician.builder().email("musician2@example.com").build();
        Project project = Project.builder().id(projectId).name("Updated Project").projectMembers(Set.of(musician1, musician2)).build();

        when(projectService.getProjectById(projectId)).thenReturn(project);

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/update", projectId)
                        .param("name", "Updated Project")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-05")
                        .param("location", "Berlin")
                        .param("notifyMembers", "true")
                        .param("updateMessage", "Rehearsal time changed.")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/" + projectId));

        verify(projectService, times(1)).updateBasicProjectInfo(eq(projectId), any(ProjectBasicInfoDTO.class));
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
        verify(emailService, times(1)).sendEmail(eq("musician1@example.com"), anyString(), anyString());
        verify(emailService, times(1)).sendEmail(eq("musician2@example.com"), anyString(), anyString());
    }

    @Test
    void updateInstrumentConfig_whenPostRequest_thenShouldSaveAndRedirect() throws Exception {
        // given
        Long projectId = 1L;
        Project project = new Project();
        when(projectService.getProjectById(projectId)).thenReturn(project);

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/instrumentCount", projectId)
                        .param("instrumentCounts[VIOLIN_I]", "2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/" + projectId + "/instrumentCount"));

        verify(projectService, times(1)).saveProject(any(Project.class));
    }
}
