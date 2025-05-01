package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.*;
import com.monika.worek.orchestra.repository.SurveyQuestionRepository;
import com.monika.worek.orchestra.repository.SurveyRepository;
import com.monika.worek.orchestra.repository.SurveySubmissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SurveyService {

    private final SurveySubmissionRepository surveySubmissionRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;

    public SurveyService(SurveySubmissionRepository surveySubmissionRepository, SurveyRepository surveyRepository, SurveyQuestionRepository surveyQuestionRepository) {
        this.surveySubmissionRepository = surveySubmissionRepository;
        this.surveyRepository = surveyRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
    }

    public boolean existsBySurveyAndMusician(Survey survey, Musician musician) {
        return surveySubmissionRepository.existsBySurveyAndMusician(survey, musician);
    }

    public long calculateMissingSubmissions(Survey survey, Project project) {
        long count = surveySubmissionRepository.countBySurvey(survey);
        return project.getProjectMembers().size() - count;
    }

    public Optional<Survey> findByProjectId(Long id) {
        return surveyRepository.findByProjectId(id);
    }

    @Transactional
    public void saveQuestions(List<SurveyQuestion> questions) {
        surveyQuestionRepository.saveAll(questions);
    }

    @Transactional
    public void saveSubmission(SurveySubmission surveySubmission) {
        surveySubmissionRepository.save(surveySubmission);
    }

    @Transactional
    public void saveSurvey(Survey survey) {
        surveyRepository.save(survey);
    }
}
