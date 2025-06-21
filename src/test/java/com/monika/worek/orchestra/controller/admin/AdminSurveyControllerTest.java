package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.Survey;
import com.monika.worek.orchestra.service.ProjectService;
import com.monika.worek.orchestra.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminSurveyController.class)
@WithMockUser(username = "admin@example.com", roles = "ADMIN")
class AdminSurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyService surveyService;

    @MockBean
    private ProjectService projectService;

    private Project project;
    private Survey survey;

    @BeforeEach
    void setUp() {
        project = Project.builder().id(1L).name("Test Project").build();
        survey = new Survey();
        survey.setId(10L);
        survey.setProject(project);
        project.setSurvey(survey);
    }

    @Test
    void showSurveyQuestions_whenGetRequest_thenShouldReturnViewWithSurveyData() throws Exception {
        // given
        when(projectService.getProjectById(1L)).thenReturn(project);
        when(surveyService.findAllQuestionsBySurvey(any(Survey.class))).thenReturn(Collections.emptyList());
        when(surveyService.calculateMissingSubmissions(any(Survey.class), any(Project.class))).thenReturn(5L);

        // when
        // then
        mockMvc.perform(get("/admin/project/{projectId}/survey", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin-survey"))
                .andExpect(model().attributeExists("projectId", "projectName", "survey", "questions", "surveyQuestionDTO", "missingSubmissions"))
                .andExpect(model().attribute("missingSubmissions", 5L));
    }

    @Test
    void addSurveyQuestion_whenDataIsValid_thenShouldSaveQuestionAndRedirect() throws Exception {
        // given
        when(projectService.getProjectById(1L)).thenReturn(project);
        when(surveyService.findAllQuestionsBySurvey(any(Survey.class))).thenReturn(Collections.emptyList());

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/survey", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("questionText", "Vegetarian meal?")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/1/survey"))
                .andExpect(flash().attribute("success", "Survey question added."));

        verify(surveyService, times(1)).saveSurvey(any(Survey.class));
    }

    @Test
    void addSurveyQuestion_whenDataIsInvalid_thenShouldReturnFormViewWithErrors() throws Exception {
        // given
        when(projectService.getProjectById(1L)).thenReturn(project);
        when(surveyService.findAllQuestionsBySurvey(any(Survey.class))).thenReturn(Collections.emptyList());

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/survey", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("questionText", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin-survey"))
                .andExpect(model().attributeHasFieldErrors("surveyQuestionDTO", "questionText"));

        verify(surveyService, never()).saveSurvey(any());
    }

    @Test
    void deleteSurveyQuestion_whenPostRequest_thenShouldDeleteAndRedirect() throws Exception {
        // given
        Long projectId = 1L;
        Long questionId = 101L;

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/survey/{questionId}/delete", projectId, questionId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/" + projectId + "/survey"))
                .andExpect(flash().attribute("success", "Survey question deleted."));

        verify(surveyService, times(1)).deleteQuestionById(questionId);
    }

    @Test
    void toggleSurveyStatus_whenSurveyIsOpen_thenShouldCloseAndRedirect() throws Exception {
        // given
        survey.setClosed(false);
        when(projectService.getProjectById(1L)).thenReturn(project);

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/survey/toggle", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/1/survey"))
                .andExpect(flash().attribute("success", "Survey has been closed."));

        verify(projectService, times(1)).saveProject(project);
        assertTrue(survey.isClosed());
    }

    @Test
    void toggleSurveyStatus_whenSurveyIsClosed_thenShouldReopenAndRedirect() throws Exception {
        // given
        survey.setClosed(true);
        when(projectService.getProjectById(1L)).thenReturn(project);

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/survey/toggle", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/1/survey"))
                .andExpect(flash().attribute("success", "Survey has been reopened."));

        verify(projectService, times(1)).saveProject(project);
        assertFalse(survey.isClosed());
    }
}