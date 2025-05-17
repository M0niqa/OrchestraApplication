package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.MusicianAgreement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicianAgreementRepository extends CrudRepository<MusicianAgreement, Long> {

    Optional<MusicianAgreement> findByMusicianIdAndProjectId(Long musicianId, Long projectId);
    List<MusicianAgreement> findByProjectId(Long projectId);
    void deleteByMusicianIdAndProjectId(Long musicianId, Long projectId);
}
