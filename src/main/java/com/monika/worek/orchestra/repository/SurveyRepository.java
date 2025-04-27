package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Survey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyRepository extends CrudRepository<Survey, Long> {

    Optional<Survey> findByProjectId(Long projectId);
}