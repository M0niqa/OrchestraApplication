package com.monika.worek.orchestra.repository;


import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MusicianRepository extends CrudRepository<Musician, Long> {

    Optional<Musician> findByEmail(String email);

    @Query("SELECT p FROM Musician m JOIN m.acceptedProjects p WHERE m.id = :musicianId AND p.endDate >= :today")
    List<Project> findActiveAcceptedProjects(@Param("musicianId") Long musicianId, @Param("today") LocalDate today);

    @Query("SELECT p FROM Musician m JOIN m.pendingProjects p WHERE m.id = :musicianId AND p.endDate >= :today")
    List<Project> findActivePendingProjects(@Param("musicianId") Long musicianId, @Param("today") LocalDate today);

    @Query("SELECT p FROM Musician m JOIN m.rejectedProjects p WHERE m.id = :musicianId AND p.endDate >= :today")
    List<Project> findActiveRejectedProjects(@Param("musicianId") Long musicianId, @Param("today") LocalDate today);

    @Query("SELECT p FROM Musician m JOIN m.acceptedProjects p WHERE m.id = :musicianId AND p.endDate < :today")
    List<Project> findArchivedAcceptedProjects(@Param("musicianId") Long musicianId, @Param("today") LocalDate today);

    @Query("SELECT p FROM Musician m JOIN m.rejectedProjects p WHERE m.id = :musicianId AND p.endDate < :today")
    List<Project> findArchivedRejectedProjects(@Param("musicianId") Long musicianId, @Param("today") LocalDate today);

}
