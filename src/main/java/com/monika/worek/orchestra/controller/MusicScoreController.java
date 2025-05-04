package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.MusicScoreDTO;
import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicScoreRepository;
import com.monika.worek.orchestra.service.ProjectService;
import com.monika.worek.orchestra.service.FileStorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MusicScoreController {

    private final FileStorageService fileStorageService;
    private final MusicScoreRepository musicScoreRepository;
    private final ProjectService projectService;

    public MusicScoreController(FileStorageService fileStorageService, MusicScoreRepository musicScoreRepository, ProjectService projectService) {
        this.fileStorageService = fileStorageService;
        this.musicScoreRepository = musicScoreRepository;
        this.projectService = projectService;
    }

    @GetMapping("/admin/project/{projectId}/scores")
    public String manageScores(@PathVariable Long projectId, Model model) {
        List<MusicScore> files = musicScoreRepository.findByProjectId(projectId);
        List<MusicScoreDTO> fileDTOs = files.stream()
                .map(file -> new MusicScoreDTO(
                        file.getId(),
                        file.getFileName(),
                        file.getFileType(),
                        "/admin/project/" + projectId + "/scores/download/" + file.getId()
                ))
                .collect(Collectors.toList());

        model.addAttribute("projectId", projectId);
        model.addAttribute("files", fileDTOs);
        return "/admin/admin-scores";
    }

    @PostMapping("/admin/project/{projectId}/scores/upload")
    public String uploadFile(@PathVariable Long projectId,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload.");
            return "redirect:/admin/project/" + projectId + "/scores";
        }

        try {
            Project project = projectService.getProjectById(projectId);
            String filePath = fileStorageService.saveFile(file);

            MusicScore projectFile = new MusicScore(file.getOriginalFilename(), file.getContentType(), filePath, project);
            musicScoreRepository.save(projectFile);

            redirectAttributes.addFlashAttribute("success", "File uploaded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file.");
        }

        return "redirect:/admin/project/" + projectId + "/scores";
    }

    @PostMapping("/admin/project/{projectId}/scores/{fileId}/delete")
    public String deleteFile(@PathVariable Long projectId,
                             @PathVariable Long fileId,
                             RedirectAttributes redirectAttributes) {
        try {
            MusicScore musicScore = musicScoreRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            fileStorageService.deleteFile(musicScore.getFilePath());
            musicScoreRepository.delete(musicScore);

            redirectAttributes.addFlashAttribute("success", "File deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete file.");
        }

        return "redirect:/admin/project/" + projectId + "/scores";
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
    public ResponseEntity<byte[]> downloadScore(@PathVariable Long projectId, @PathVariable Long fileId) throws IOException {
        MusicScore musicScore = musicScoreRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        byte[] fileData = fileStorageService.getFile(musicScore.getFilePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + musicScore.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(musicScore.getFileType()))
                .body(fileData);
    }
}
