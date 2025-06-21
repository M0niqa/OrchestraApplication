package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicScoreRepository;
import com.monika.worek.orchestra.service.FileStorageService;
import com.monika.worek.orchestra.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MusicianScoreController.class)
@WithMockUser(username = "musician@example.com", roles = "MUSICIAN")
class MusicianScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicScoreRepository musicScoreRepository;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private ProjectService projectService;

    @TempDir
    Path tempDir;

    @Test
    void listScores_whenAuthorized_thenShouldReturnViewWithScores() throws Exception {
        // given
        Long projectId = 1L;
        String userEmail = "musician@example.com";
        Project project = Project.builder().id(projectId).build();
        List<MusicScore> scores = Collections.singletonList(new MusicScore());

        doNothing().when(projectService).throwIfUnauthorized(projectId, userEmail);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(musicScoreRepository.findByProjectId(projectId)).thenReturn(scores);

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/scores", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("/musician/musician-scores"))
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("scores", scores));
    }

    @Test
    void downloadScore_whenFileExists_thenShouldReturnFileContent() throws Exception {
        // given
        Long projectId = 1L;
        Long fileId = 101L;
        String userEmail = "musician@example.com";
        String fileName = "test-score.pdf";
        String filePath = "test-score.pdf";

        File tempFile = tempDir.resolve(filePath).toFile();
        Files.writeString(tempFile.toPath(), "pdf content");

        MusicScore score = MusicScore.builder()
                .id(fileId)
                .fileName(fileName)
                .fileType(MediaType.APPLICATION_PDF_VALUE)
                .filePath(tempFile.getAbsolutePath())
                .build();

        doNothing().when(projectService).throwIfUnauthorized(projectId, userEmail);
        when(musicScoreRepository.findById(fileId)).thenReturn(Optional.of(score));
        when(fileStorageService.getFile(tempFile.getAbsolutePath())).thenReturn(tempFile);

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/scores/download/{fileId}", projectId, fileId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + fileName + "\""))
                .andExpect(content().string("pdf content"));
    }

    @Test
    void downloadScore_whenFileNotFoundInDb_thenShouldReturnNotFound() throws Exception {
        // given
        Long projectId = 1L;
        Long fileId = 999L;
        String userEmail = "musician@example.com";

        doNothing().when(projectService).throwIfUnauthorized(projectId, userEmail);
        when(musicScoreRepository.findById(fileId)).thenReturn(Optional.empty());

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/scores/download/{fileId}", projectId, fileId))
                .andExpect(status().isNotFound());
    }
}