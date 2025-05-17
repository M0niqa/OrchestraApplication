package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not initialize storage location: " + this.fileStorageLocation, ex);
        }
    }

    public File getFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            throw new FileNotFoundException("File does not exist or cannot be read: " + filePath);
        }
        return file;
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String baseName = (originalFileName != null)
                ? originalFileName.substring(0, originalFileName.length() - extension.length()).replaceAll("[^a-zA-Z0-9.-]", "_")
                : "file";
        return UUID.randomUUID() + "_" + baseName + extension;
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Failed to store file " + uniqueFileName + ". Error: " + ex.getMessage(), ex);
        }

        return targetLocation.toString();
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath).normalize();
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new IOException("Could not delete file: " + filePath, ex);
        }
    }

    public String saveGeneratedAgreement(byte[] pdf, Musician musician, Project project) {
        try {
            String sanitizedName = musician.getLastName().replaceAll("[^a-zA-Z0-9]", "_");
            String projectName = project.getName().replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = projectName + "_" + sanitizedName + "_agreement.pdf";

            Path targetPath = this.fileStorageLocation.resolve(fileName);
            Files.write(targetPath, pdf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return targetPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save agreement PDF", e);
        }
    }
}