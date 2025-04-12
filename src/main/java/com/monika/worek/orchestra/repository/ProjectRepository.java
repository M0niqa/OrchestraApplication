package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    List<Project> findAllByStatus(Status status);
}
