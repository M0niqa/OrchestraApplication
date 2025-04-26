package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.Survey;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SurveyRepository extends CrudRepository<Survey, Long> {
    Optional<Survey> findByMusicianAndProject(Musician musician, Project project);
}