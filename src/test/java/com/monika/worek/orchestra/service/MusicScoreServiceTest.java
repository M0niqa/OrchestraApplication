package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.repository.MusicScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicScoreServiceTest {

    @Mock
    private MusicScoreRepository musicScoreRepository;

    @InjectMocks
    private MusicScoreService musicScoreService;

    @Test
    void findScoreById_whenScoreExists_thenShouldReturnScore() {
        // given
        Long scoreId = 1L;
        MusicScore expectedScore = new MusicScore();
        when(musicScoreRepository.findById(scoreId)).thenReturn(Optional.of(expectedScore));

        // when
        MusicScore actualScore = musicScoreService.findScoreById(scoreId);

        // then
        assertThat(actualScore).isEqualTo(expectedScore);
    }

    @Test
    void findScoreById_whenScoreDoesNotExist_thenShouldThrowException() {
        // given
        Long scoreId = 99L;
        when(musicScoreRepository.findById(scoreId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> musicScoreService.findScoreById(scoreId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("File not found");
    }

    @Test
    void findScoresByProjectId_whenCalled_thenShouldReturnRepositoryResult() {
        // given
        Long projectId = 1L;
        List<MusicScore> expectedScores = List.of(new MusicScore(), new MusicScore());
        when(musicScoreRepository.findByProjectId(projectId)).thenReturn(expectedScores);

        // when
        List<MusicScore> actualScores = musicScoreService.findScoresByProjectId(projectId);

        // then
        assertThat(actualScores).isEqualTo(expectedScores);
    }

    @Test
    void saveScore_whenCalled_thenShouldInvokeRepositorySave() {
        // given
        MusicScore scoreToSave = new MusicScore();

        // when
        musicScoreService.saveScore(scoreToSave);

        // then
        verify(musicScoreRepository, times(1)).save(scoreToSave);
    }

    @Test
    void deleteScore_whenCalled_thenShouldInvokeRepositoryDelete() {
        // given
        MusicScore scoreToDelete = new MusicScore();

        // when
        musicScoreService.deleteScore(scoreToDelete);

        // then
        verify(musicScoreRepository, times(1)).delete(scoreToDelete);
    }
}
