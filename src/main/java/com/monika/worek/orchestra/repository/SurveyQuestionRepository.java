package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Survey;
import com.monika.worek.orchestra.model.SurveyQuestion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyQuestionRepository extends CrudRepository<SurveyQuestion, Long> {
    List<SurveyQuestion> findAllBySurvey(Survey survey);
}
