package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.Survey;
import com.monika.worek.orchestra.model.SurveyQuestion;
import com.monika.worek.orchestra.service.MusicianService;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MusicianSurveyController.class)
@WithMockUser(username = "musician@example.com", roles = "MUSICIAN")
class MusicianSurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicianService musicianService;

    @MockBean
    private SurveyService surveyService;

    @MockBean
    private ProjectService projectService;

    private Musician musician;
    private Survey survey;
    private final String userEmail = "musician@example.com";
    private final Long projectId = 1L;

    @BeforeEach
    void setUp() {
        musician = Musician.builder().id(1L).email(userEmail).build();
        Project project = Project.builder().id(projectId).name("Test Project").build();
        survey = new Survey();
        survey.setId(10L);
        survey.setProject(project);
        survey.setQuestions(List.of(new SurveyQuestion()));
    }

    @Test
    void showSurvey_whenSurveyIsOpenAndNotSubmitted_thenShouldReturnSurveyView() throws Exception {
        // given
        when(musicianService.getMusicianByEmail(userEmail)).thenReturn(musician);
        when(surveyService.findByProjectId(projectId)).thenReturn(Optional.of(survey));
        when(surveyService.existsBySurveyAndMusician(survey, musician)).thenReturn(false);
        survey.setClosed(false);

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/survey", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-survey"))
                .andExpect(model().attributeExists("submissionDTO", "questions"))
                .andExpect(model().attribute("surveySubmitted", false));
    }

    @Test
    void showSurvey_whenSurveyIsClosed_thenShouldReturnViewWithClosedFlag() throws Exception {
        // given
        when(musicianService.getMusicianByEmail(userEmail)).thenReturn(musician);
        when(surveyService.findByProjectId(projectId)).thenReturn(Optional.of(survey));
        survey.setClosed(true);

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/survey", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-survey"))
                .andExpect(model().attribute("surveyClosed", true));
    }

    @Test
    void showSurvey_whenSurveyAlreadySubmitted_thenShouldReturnViewWithSubmittedFlag() throws Exception {
        // given
        when(musicianService.getMusicianByEmail(userEmail)).thenReturn(musician);
        when(surveyService.findByProjectId(projectId)).thenReturn(Optional.of(survey));
        when(surveyService.existsBySurveyAndMusician(survey, musician)).thenReturn(true);
        survey.setClosed(false);

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/survey", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-survey"))
                .andExpect(model().attribute("surveySubmitted", true));
    }

    @Test
    void showSurvey_whenNoSurveyExists_thenShouldReturnViewWithNoSurveyFlag() throws Exception {
        // given
        when(musicianService.getMusicianByEmail(userEmail)).thenReturn(musician);
        when(surveyService.findByProjectId(projectId)).thenReturn(Optional.empty());

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/survey", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-survey"))
                .andExpect(model().attribute("noSurvey", true));
    }

    @Test
    void submitSurvey_whenCalled_thenShouldUpdateQuestionsAndSaveSubmission() throws Exception {
        // given
        SurveyQuestion question1 = new SurveyQuestion();
        question1.setId(101L);
        question1.setYesCount(5);
        question1.setNoCount(2);
        survey.setQuestions(List.of(question1));

        when(musicianService.getMusicianByEmail(userEmail)).thenReturn(musician);
        when(surveyService.findByProjectId(projectId)).thenReturn(Optional.of(survey));
        when(surveyService.findQuestionById(101L)).thenReturn(question1);

        // when
        // then
        mockMvc.perform(post("/musician/project/{projectId}/survey", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("responses[101]", "YES"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/musician/project/1/survey"))
                .andExpect(flash().attribute("submissionSuccess", "survey submitted successfully!"));

        assertEquals(6, question1.getYesCount());

        verify(surveyService, times(1)).saveQuestions(anyList());
        verify(surveyService, times(1)).saveSubmission(any());
    }
}