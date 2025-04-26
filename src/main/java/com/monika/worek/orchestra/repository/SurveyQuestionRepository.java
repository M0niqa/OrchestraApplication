package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.SurveyQuestion;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SurveyQuestionRepository extends CrudRepository<SurveyQuestion, Long> {

    List<SurveyQuestion> findByProject(Project project);
}
