package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.auth.MusicScoreDTO;
import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicScoreRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import com.monika.worek.orchestra.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("admin/project/{projectId}/scores")
public class MusicScoreController {

    private final FileStorageService fileStorageService;
    private final MusicScoreRepository musicScoreRepository;
    private final ProjectRepository projectRepository;


    public MusicScoreController(FileStorageService fileStorageService, MusicScoreRepository musicScoreRepository, ProjectRepository projectRepository) {
        this.fileStorageService = fileStorageService;
        this.musicScoreRepository = musicScoreRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public String manageScores(@PathVariable Long projectId, Model model) {
        List<MusicScore> files = musicScoreRepository.findByProjectId(projectId);
        List<MusicScoreDTO> fileDTOs = files.stream()
                .map(file -> new MusicScoreDTO(
                        file.getId(),
                        file.getFileName(),
                        file.getFileType(),
                        "/project/" + projectId + "/scores/download/" + file.getId()
                ))
                .collect(Collectors.toList());

        model.addAttribute("projectId", projectId);
        model.addAttribute("files", fileDTOs);
        return "scores";
    }

    @PostMapping("/upload")
    public String uploadFile(@PathVariable Long projectId,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload.");
            return "redirect:/project/" + projectId + "/scores";
        }

        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            String filePath = fileStorageService.saveFile(file);

            MusicScore projectFile = new MusicScore(file.getOriginalFilename(), file.getContentType(), filePath, project);
            musicScoreRepository.save(projectFile);

            redirectAttributes.addFlashAttribute("success", "File uploaded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file.");
        }

        return "redirect:/project/" + projectId + "/scores";
    }

    @PostMapping("/{fileId}/delete")
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

        return "redirect:/project/" + projectId + "/scores";
    }

}
