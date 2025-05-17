package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.MusicianAgreement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementRepository extends CrudRepository<MusicianAgreement, Long> {

    MusicianAgreement findByMusicianIdAndProjectId(Long musicianId, Long projectId);
    List<MusicianAgreement> findByProjectId(Long projectId);
}
