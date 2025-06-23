package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Survey;
import com.monika.worek.orchestra.model.SurveySubmission;
import org.springframework.data.repository.CrudRepository;

public interface SurveySubmissionRepository extends CrudRepository<SurveySubmission, Long> {

    boolean existsBySurveyAndMusician(Survey survey, Musician musician);
    long countBySurvey(Survey survey);
    void deleteByMusicianId(Long musicianId);
}
