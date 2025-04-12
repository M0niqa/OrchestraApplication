package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.MusicScore;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicScoreRepository extends CrudRepository<MusicScore, Long> {
    List<MusicScore> findByProjectId(Long projectId);
}
