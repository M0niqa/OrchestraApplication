package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicScoreRepository;
import com.monika.worek.orchestra.service.FileStorageService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class MusicianScoreController {

    private final MusicScoreRepository musicScoreRepository;
    private final FileStorageService fileStorageService;
    private final ProjectService projectService;

    public MusicianScoreController(MusicScoreRepository musicScoreRepository, FileStorageService fileStorageService, ProjectService projectService) {
        this.musicScoreRepository = musicScoreRepository;
        this.fileStorageService = fileStorageService;
        this.projectService = projectService;
    }

    @GetMapping("/musician/project/{projectId}/scores")
    public String listScores(@PathVariable Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);

        List<MusicScore> scores = musicScoreRepository.findByProjectId(projectId);
        model.addAttribute("project", project);
        model.addAttribute("scores", scores);

        return "/musician/musician-scores";
    }

    @GetMapping({"/admin/project/{projectId}/scores/download/{fileId}",
            "/musician/project/{projectId}/scores/download/{fileId}"})
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadScore(@PathVariable Long projectId, @PathVariable Long fileId) throws IOException {
        MusicScore musicScore = musicScoreRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));

        File file = fileStorageService.getFile(musicScore.getFilePath());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + musicScore.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(musicScore.getFileType()))
                .contentLength(file.length())
                .body(resource);
    }

}
