package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.Survey;
import com.monika.worek.orchestra.model.SurveySubmission;
import com.monika.worek.orchestra.repository.SurveySubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    private final SurveySubmissionRepository surveySubmissionRepository;

    public SurveyService(SurveySubmissionRepository surveySubmissionRepository) {
        this.surveySubmissionRepository = surveySubmissionRepository;
    }

    public boolean existsBySurveyAndMusician(Survey survey, Musician musician) {
        return surveySubmissionRepository.existsBySurveyAndMusician(survey, musician);
    }

    public long calculateMissingSubmissions(Survey survey, Project project) {
        long count = surveySubmissionRepository.countBySurvey(survey);
        return project.getProjectMembers().size() - count;
    }

    public void saveSubmission(SurveySubmission surveySubmission) {
        surveySubmissionRepository.save(surveySubmission);
    }
}
