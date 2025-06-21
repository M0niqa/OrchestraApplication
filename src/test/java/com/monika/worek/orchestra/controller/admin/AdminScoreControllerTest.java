package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.FileStorageService;
import com.monika.worek.orchestra.service.MusicScoreService;
import com.monika.worek.orchestra.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminScoreController.class)
@WithMockUser(username = "admin@example.com", roles = "ADMIN")
class AdminScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private MusicScoreService musicScoreService;

    @MockBean
    private ProjectService projectService;

    @Test
    void manageScores_whenGetRequest_thenShouldReturnViewWithFiles() throws Exception {
        // given
        Long projectId = 1L;
        MusicScore score1 = MusicScore.builder().id(101L).fileName("score1.pdf").fileType("application/pdf").build();
        when(musicScoreService.findScoresByProjectId(projectId)).thenReturn(List.of(score1));

        // when
        // then
        mockMvc.perform(get("/admin/project/{projectId}/scores", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin-scores"))
                .andExpect(model().attributeExists("projectId", "files"))
                .andExpect(model().attribute("projectId", projectId));
    }

    @Test
    void uploadFile_whenFileIsValid_thenShouldSaveAndRedirectWithSuccess() throws Exception {
        // given
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "music.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "content".getBytes()
        );

        when(projectService.getProjectById(projectId)).thenReturn(new Project());
        when(fileStorageService.saveFile(file)).thenReturn("/path/to/music.pdf");

        // when
        // then
        mockMvc.perform(multipart("/admin/project/{projectId}/scores/upload", projectId)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/" + projectId + "/scores"))
                .andExpect(flash().attribute("success", "File uploaded successfully."));

        verify(musicScoreService, times(1)).saveScore(any(MusicScore.class));
    }

    @Test
    void uploadFile_whenFileIsEmpty_thenShouldRedirectWithError() throws Exception {
        // given
        Long projectId = 1L;
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "text/plain", new byte[0]);

        // when
        // then
        mockMvc.perform(multipart("/admin/project/{projectId}/scores/upload", projectId)
                        .file(emptyFile)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/" + projectId + "/scores"))
                .andExpect(flash().attribute("error", "Please select a file to upload."));
    }

    @Test
    void uploadFile_whenFileTypeIsUnsupported_thenShouldRedirectWithError() throws Exception {
        // given
        Long projectId = 1L;
        MockMultipartFile wrongTypeFile = new MockMultipartFile("file", "archive.zip", "application/zip", "content".getBytes());

        // when
        // then
        mockMvc.perform(multipart("/admin/project/{projectId}/scores/upload", projectId)
                        .file(wrongTypeFile)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void deleteFile_whenSuccessful_thenShouldDeleteAndRedirectWithSuccess() throws Exception {
        // given
        Long projectId = 1L;
        Long fileId = 101L;
        MusicScore score = MusicScore.builder().id(fileId).filePath("/path/to/file.pdf").build();

        when(musicScoreService.findScoreById(fileId)).thenReturn(score);
        doNothing().when(fileStorageService).deleteFile(anyString());
        doNothing().when(musicScoreService).deleteScore(any(MusicScore.class));

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/scores/{fileId}/delete", projectId, fileId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/" + projectId + "/scores"))
                .andExpect(flash().attribute("success", "File deleted successfully."));

        verify(fileStorageService, times(1)).deleteFile(score.getFilePath());
        verify(musicScoreService, times(1)).deleteScore(score);
    }

    @Test
    void deleteFile_whenFileDeletionFails_thenShouldRedirectWithError() throws Exception {
        // given
        Long projectId = 1L;
        Long fileId = 101L;
        MusicScore score = MusicScore.builder().id(fileId).filePath("/path/to/file.pdf").build();

        when(musicScoreService.findScoreById(fileId)).thenReturn(score);
        doThrow(new IOException("Disk error")).when(fileStorageService).deleteFile(anyString());

        // when
        // then
        mockMvc.perform(post("/admin/project/{projectId}/scores/{fileId}/delete", projectId, fileId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project/" + projectId + "/scores"))
                .andExpect(flash().attribute("error", "Failed to delete physical file. Database not modified."));

        verify(musicScoreService, never()).deleteScore(any());
    }
}
