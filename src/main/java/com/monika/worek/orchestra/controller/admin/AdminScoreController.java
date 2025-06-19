package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.dto.MusicScoreDTO;
import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.FileStorageService;
import com.monika.worek.orchestra.service.MusicScoreService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminScoreController {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final FileStorageService fileStorageService;
    private final MusicScoreService musicScoreService;
    private final ProjectService projectService;

    public AdminScoreController(FileStorageService fileStorageService, MusicScoreService musicScoreService, ProjectService projectService) {
        this.fileStorageService = fileStorageService;
        this.musicScoreService = musicScoreService;
        this.projectService = projectService;
    }

    @GetMapping("/admin/project/{projectId}/scores")
    public String manageScores(@PathVariable Long projectId, Model model) {
        List<MusicScore> files = musicScoreService.findScoresByProjectId(projectId);
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
            String fileType = file.getContentType();
            if (!List.of("application/pdf", "image/png", "image/jpeg").contains(fileType)) {
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File too large");
            }
            Project project = projectService.getProjectById(projectId);
            String filePath = fileStorageService.saveFile(file);

            MusicScore scoreFile = MusicScore.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .filePath(filePath)
                    .project(project)
                    .build();

            musicScoreService.saveScore(scoreFile);

            redirectAttributes.addFlashAttribute("success", "File uploaded successfully.");
        }  catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file.");
        }
        return "redirect:/admin/project/" + projectId + "/scores";
    }

    @PostMapping("/admin/project/{projectId}/scores/{fileId}/delete")
    public String deleteFile(@PathVariable Long projectId,
                             @PathVariable Long fileId,
                             RedirectAttributes redirectAttributes) {
        try {
            MusicScore musicScore = musicScoreService.findScoreById(fileId);

            try {
                fileStorageService.deleteFile(musicScore.getFilePath());
            } catch (IOException ex) {
                redirectAttributes.addFlashAttribute("error", "Failed to delete physical file. Database not modified.");
                return "redirect:/admin/project/" + projectId + "/scores";
            }

            musicScoreService.deleteScore(musicScore);
            redirectAttributes.addFlashAttribute("success", "File deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete file.");
        }

        return "redirect:/admin/project/" + projectId + "/scores";
    }
}
