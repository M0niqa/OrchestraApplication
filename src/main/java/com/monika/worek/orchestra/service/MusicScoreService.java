package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.repository.MusicScoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MusicScoreService {

    private final MusicScoreRepository musicScoreRepository;

    public MusicScoreService(MusicScoreRepository musicScoreRepository) {
        this.musicScoreRepository = musicScoreRepository;
    }

    public MusicScore findScoreById(Long id) {
        return musicScoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    public List<MusicScore> findScoresByProjectId(Long projectId) {
        return musicScoreRepository.findByProjectId(projectId);
    }

    public void saveScore(MusicScore musicScore) {
        musicScoreRepository.save(musicScore);
    }

    @Transactional
    public void deleteScore(MusicScore musicScore) {
        musicScoreRepository.delete(musicScore);
    }
}
