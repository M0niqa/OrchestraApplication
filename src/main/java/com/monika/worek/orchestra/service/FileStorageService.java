package com.monika.worek.orchestra.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException; // For specific exception handling
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path; // Use Path
import java.nio.file.Paths; // Use Paths
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path fileStorageLocation; // Store as Path object

    // Inject the path from application.properties
    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        // Resolve to absolute, normalized path
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        log.info("File storage location initialized to: {}", this.fileStorageLocation);
    }

    // This method runs after the bean is created
    @PostConstruct
    public void init() {
        try {
            // Create the directory structure if it doesn't exist
            Files.createDirectories(this.fileStorageLocation);
            log.info("Storage directory checked/created successfully: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            // Fail fast if the directory cannot be created on startup
            log.error("Could not initialize storage location: {}", this.fileStorageLocation, ex);
            throw new RuntimeException("Could not initialize storage location: " + this.fileStorageLocation, ex);
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Generate a unique filename (keeping original extension if possible)
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        // Basic sanitization (replace spaces, etc.) - can be enhanced
        String sanitizedOriginal = (originalFileName != null) ?
                originalFileName.substring(0, originalFileName.length() - extension.length()).replaceAll("[^a-zA-Z0-9.-]", "_")
                : "file";
        String uniqueFileName = UUID.randomUUID().toString() + "_" + sanitizedOriginal + extension;


        // Resolve the final absolute path for the file
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
        log.debug("Resolved target file location: {}", targetLocation);

        try {
            // Use Files.copy - often more reliable than transferTo
            // Copies the input stream from the uploaded file to the target path
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully stored file: {}", targetLocation);
        } catch (IOException ex) {
            log.error("Failed to store file {}. Target: {}", uniqueFileName, targetLocation, ex);
            // Re-throw or handle appropriately
            throw new IOException("Failed to store file " + uniqueFileName + ". Error: " + ex.getMessage(), ex);
        }

        // Return the FULL ABSOLUTE PATH to store in the database
        return targetLocation.toString();
    }

    public byte[] getFile(String filePath) throws IOException {
        Path path = Paths.get(filePath).normalize(); // Normalize path from DB
        log.debug("Loading file from path: {}", path);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            log.error("File not found or not readable at resolved path: {}", path);
            throw new FileNotFoundException("Requested file not found or cannot be read: " + filePath);
        }
        return Files.readAllBytes(path);
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath).normalize(); // Normalize path from DB
        log.debug("Deleting file from path: {}", path);
        try {
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.info("Successfully deleted file: {}", path);
            } else {
                log.warn("Attempted to delete non-existent file: {}", path);
            }
        } catch (IOException ex) {
            log.error("Could not delete file: {}", path, ex);
            throw new IOException("Could not delete file: " + filePath, ex);
        }
    }
}