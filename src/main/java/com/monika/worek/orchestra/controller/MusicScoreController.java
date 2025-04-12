package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.auth.MusicScoreDTO;
import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicScoreRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import com.monika.worek.orchestra.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects/{projectId}/files")
public class MusicScoreController {

    private FileStorageService fileStorageService;
    private MusicScoreRepository musicScoreRepository;
    private ProjectRepository projectRepository;

    private static final Logger log = LoggerFactory.getLogger(MusicScoreController.class); // Add logger instance


    public MusicScoreController(FileStorageService fileStorageService, MusicScoreRepository musicScoreRepository, ProjectRepository projectRepository) {
        this.fileStorageService = fileStorageService;
        this.musicScoreRepository = musicScoreRepository;
        this.projectRepository = projectRepository;
    }

    @PostMapping("/upload")
    @ResponseBody // Keep this as it returns data
    public ResponseEntity<?> uploadFile(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) {
        // --- VERY IMPORTANT LOGGING ---
        log.info(">>> ENTERING uploadFile - Project ID: {}, File Name: {}, Content Type: {}, Size: {}",
                projectId, file.getOriginalFilename(), file.getContentType(), file.getSize());

        if (file.isEmpty()) {
            log.warn("!!! File upload attempt with empty file for Project ID: {}", projectId);
            // Consider returning a Bad Request response
            return ResponseEntity.badRequest().body("Cannot upload an empty file.");
        }

        try {
            log.info("Step 1: Finding project with ID: {}", projectId);
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        // Log before throwing
                        log.error("!!! RuntimeException: Project not found with ID: {}", projectId);
                        return new RuntimeException("Project not found with ID: " + projectId);
                    });
            log.info("Step 1 SUCCESS: Found project {}", project.getId()); // Assuming Project has getId()

            log.info("Step 2: Calling fileStorageService.saveFile for '{}'", file.getOriginalFilename());
            String filePath = fileStorageService.saveFile(file); // This might throw IOException
            log.info("Step 2 SUCCESS: File saved to path: {}", filePath);

            log.info("Step 3: Creating MusicScore entity");
            MusicScore projectFile = new MusicScore(file.getOriginalFilename(), file.getContentType(), filePath, project);
            log.info("Step 3 SUCCESS: MusicScore entity created");

            log.info("Step 4: Saving MusicScore entity to database");
            musicScoreRepository.save(projectFile); // This might throw DataAccessException etc.
            log.info("Step 4 SUCCESS: MusicScore saved with DB ID: {}", projectFile.getId());

            String downloadUrl = "/projects/" + projectId + "/files/download/" + projectFile.getId();
            MusicScoreDTO fileDTO = new MusicScoreDTO(projectFile.getId(), projectFile.getFileName(), projectFile.getFileType(), downloadUrl);

            log.info("<<< EXITING uploadFile - SUCCESS - Project ID: {}", projectId);
            return ResponseEntity.ok(fileDTO);

        } catch (IOException ioEx) {
            log.error("!!! IOException in uploadFile - Project ID: {}. Error saving file '{}'. Message: {}",
                    projectId, file.getOriginalFilename(), ioEx.getMessage(), ioEx); // Log the exception fully
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save file: IO Error."); // Return meaningful error message

        } catch (RuntimeException rtEx) {
            log.error("!!! RuntimeException in uploadFile - Project ID: {}. Message: {}",
                    projectId, rtEx.getMessage(), rtEx); // Log the exception fully
            // Check if it's the project not found error specifically
            if (rtEx.getMessage() != null && rtEx.getMessage().contains("Project not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rtEx.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal processing error.");

        } catch (Exception e) {
            // Catch-all for any other unexpected exceptions
            log.error("!!! UNEXPECTED Exception in uploadFile - Project ID: {}. Type: {}. Message: {}",
                    projectId, e.getClass().getName(), e.getMessage(), e); // Log the exception fully
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during upload.");
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) throws IOException {
        MusicScore musicScore = musicScoreRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        byte[] fileData = fileStorageService.getFile(musicScore.getFilePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + musicScore.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(musicScore.getFileType()))
                .body(fileData);
    }

    @GetMapping
    public ResponseEntity<List<MusicScoreDTO>> listMusicScores(@PathVariable Long projectId) {
        List<MusicScore> files = musicScoreRepository.findByProjectId(projectId);

        List<MusicScoreDTO> fileDTOs = files.stream()
                .map(file -> new MusicScoreDTO(
                        file.getId(),
                        file.getFileName(),
                        file.getFileType(),
                        "/projects/" + projectId + "/files/download/" + file.getId()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(fileDTOs);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        try {
            MusicScore musicScore = musicScoreRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            fileStorageService.deleteFile(musicScore.getFilePath());
            musicScoreRepository.delete(musicScore);

            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File deletion failed: " + e.getMessage());
        }
    }

    @GetMapping("/view")
    public String showMusicScores(@PathVariable Long projectId, Model model) {
        List<MusicScore> files = musicScoreRepository.findByProjectId(projectId);

        List<MusicScoreDTO> fileDTOs = files.stream()
                .map(file -> new MusicScoreDTO(
                        file.getId(),
                        file.getFileName(),
                        file.getFileType(),
                        "/projects/" + projectId + "/files/download/" + file.getId()
                ))
                .collect(Collectors.toList());

        model.addAttribute("projectId", projectId);
        model.addAttribute("files", fileDTOs);
        return "musicScore"; // Renders project-files.html
    }

    @GetMapping("/upload-form")
    public String showUploadForm(@PathVariable Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "musicScore-upload"; // Make sure you create this HTML file
    }

}
