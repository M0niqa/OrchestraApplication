package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.*;
import com.monika.worek.orchestra.repository.SurveyQuestionRepository;
import com.monika.worek.orchestra.repository.SurveyRepository;
import com.monika.worek.orchestra.repository.SurveySubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {

    @Mock
    private SurveySubmissionRepository surveySubmissionRepository;
    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private SurveyQuestionRepository surveyQuestionRepository;

    @InjectMocks
    private SurveyService surveyService;

    @Test
    void findAllQuestionsBySurvey_whenCalled_thenShouldReturnRepositoryResult() {
        // given
        Survey survey = new Survey();
        List<SurveyQuestion> expectedQuestions = List.of(new SurveyQuestion(), new SurveyQuestion());
        when(surveyQuestionRepository.findAllBySurvey(survey)).thenReturn(expectedQuestions);

        // when
        List<SurveyQuestion> actualQuestions = surveyService.findAllQuestionsBySurvey(survey);

        // then
        assertThat(actualQuestions).isEqualTo(expectedQuestions);
    }

    @Test
    void findByProjectId_whenSurveyExists_thenShouldReturnOptionalOfSurvey() {
        // given
        Long projectId = 1L;
        Survey expectedSurvey = new Survey();
        expectedSurvey.setId(10L);

        when(surveyRepository.findByProjectId(projectId)).thenReturn(Optional.of(expectedSurvey));

        // when
        Optional<Survey> actualSurveyOpt = surveyService.findByProjectId(projectId);

        // then
        assertThat(actualSurveyOpt).isPresent();
        assertThat(actualSurveyOpt.get()).isEqualTo(expectedSurvey);
    }

    @Test
    void findByProjectId_whenSurveyDoesNotExist_thenShouldReturnEmptyOptional() {
        // given
        Long projectId = 99L;
        when(surveyRepository.findByProjectId(projectId)).thenReturn(Optional.empty());

        // when
        Optional<Survey> actualSurveyOpt = surveyService.findByProjectId(projectId);

        // then
        assertThat(actualSurveyOpt).isNotPresent();
    }

    @Test
    void findQuestionById_whenQuestionExists_thenShouldReturnSurveyQuestion() {
        // given
        Long questionId = 1L;
        SurveyQuestion expectedQuestion = new SurveyQuestion();
        expectedQuestion.setId(questionId);
        when(surveyQuestionRepository.findById(questionId)).thenReturn(Optional.of(expectedQuestion));

        // when
        SurveyQuestion actualQuestion = surveyService.findQuestionById(questionId);

        // then
        assertThat(actualQuestion).isEqualTo(expectedQuestion);
    }

    @Test
    void findQuestionById_whenQuestionDoesNotExist_thenShouldThrowException() {
        // given
        Long questionId = 99L;
        when(surveyQuestionRepository.findById(questionId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> surveyService.findQuestionById(questionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Survey question not found");
    }

    @Test
    void existsBySurveyAndMusician_whenCalled_thenShouldReturnRepositoryResult() {
        // given
        Survey survey = new Survey();
        Musician musician = new Musician();
        when(surveySubmissionRepository.existsBySurveyAndMusician(survey, musician)).thenReturn(true);

        // when
        boolean exists = surveyService.existsBySurveyAndMusician(survey, musician);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void calculateMissingSubmissions_whenSubmissionsExist_thenShouldReturnCorrectCount() {
        // given
        Set<Musician> projectMembers = new HashSet<>();
        projectMembers.add(Musician.builder().firstName("Monika").build());
        projectMembers.add(Musician.builder().firstName("Natalia").build());
        projectMembers.add(Musician.builder().firstName("Joanna").build());
        Survey survey = new Survey();
        Project project = Project.builder()
                .projectMembers(projectMembers)
                .build();

        when(surveySubmissionRepository.countBySurvey(survey)).thenReturn(1L);

        // when
        long missingCount = surveyService.calculateMissingSubmissions(survey, project);

        // then
        assertThat(missingCount).isEqualTo(2);
    }

    @Test
    void saveSurvey_whenCalled_thenShouldInvokeRepositorySave() {
        // given
        Survey surveyToSave = new Survey();

        // when
        surveyService.saveSurvey(surveyToSave);

        // then
        verify(surveyRepository, times(1)).save(surveyToSave);
    }

    @Test
    void saveQuestions_whenCalledWithList_thenShouldInvokeRepositorySaveAll() {
        // given
        List<SurveyQuestion> questionsToSave = List.of(new SurveyQuestion(), new SurveyQuestion());

        // when
        surveyService.saveQuestions(questionsToSave);

        // then
        verify(surveyQuestionRepository, times(1)).saveAll(questionsToSave);
    }

    @Test
    void saveSubmission_whenCalledWithSubmission_thenShouldInvokeRepositorySave() {
        // given
        SurveySubmission submissionToSave = new SurveySubmission();

        // when
        surveyService.saveSubmission(submissionToSave);

        // then
        verify(surveySubmissionRepository, times(1)).save(submissionToSave);
    }

    @Test
    void deleteQuestionById_whenCalled_thenShouldInvokeRepositoryDelete() {
        // given
        Long questionId = 1L;

        // when
        surveyService.deleteQuestionById(questionId);

        // then
        verify(surveyQuestionRepository, times(1)).deleteById(questionId);
    }
}
