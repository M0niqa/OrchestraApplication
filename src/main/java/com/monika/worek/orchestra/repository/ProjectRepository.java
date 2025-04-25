package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.Status;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    List<Project> findAllByStatus(Status status);

    List<Project> findByStartDateBeforeAndEndDateAfter(LocalDate date1, LocalDate date2);

    List<Project> findByStartDateAfter(LocalDate date);

    List<Project> findByEndDateBefore(LocalDate date);

}
