package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.exception.FileStorageException;
import com.monika.worek.orchestra.model.Musician;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    private final String uploadDirectory = "/test-uploads";

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(uploadDirectory);
    }

    @Test
    void saveFile_whenGivenValidFile_thenShouldReturnFilePath() throws IOException {
        // given
        byte[] content = "test content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", content);
        UUID fixedUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String expectedFileName = fixedUuid + "_test.pdf";
        Path expectedPath = Paths.get(uploadDirectory, expectedFileName);

        try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedUuid.when(UUID::randomUUID).thenReturn(fixedUuid);

            ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);

            // when
            String resultFilePath = fileStorageService.saveFile(file);

            // then
            mockedFiles.verify(() -> Files.copy(
                    inputStreamCaptor.capture(),
                    eq(expectedPath),
                    any(StandardCopyOption.class)
            ));

            InputStream capturedStream = inputStreamCaptor.getValue();
            assertThat(capturedStream.readAllBytes()).isEqualTo(content);

            assertThat(resultFilePath).isEqualTo(expectedPath.toString());
        }
    }

    @Test
    void saveFile_whenFileIsEmpty_thenShouldThrowIOException() {
        // given
        MultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        // when
        // then
        assertThatThrownBy(() -> fileStorageService.saveFile(emptyFile))
                .isInstanceOf(IOException.class)
                .hasMessage("Failed to store empty file.");
    }

    @Test
    void deleteFile_whenPathIsValid_thenShouldCallFilesDelete() {
        // given
        String filePathString = "/test-uploads/file-to-delete.pdf";
        Path expectedPath = Paths.get(filePathString);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(expectedPath)).thenReturn(true);

            // when
            assertDoesNotThrow(() -> fileStorageService.deleteFile(filePathString));

            // then
            mockedFiles.verify(() -> Files.deleteIfExists(expectedPath));
        }
    }

    @Test
    void saveGeneratedAgreement_whenGivenValidData_thenShouldReturnFilePath() {
        // given
        byte[] pdfBytes = "agreement content".getBytes();
        Musician musician = Musician.builder().lastName("Kowalski").build();

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            // when
            String filePath = fileStorageService.saveGeneratedAgreement(pdfBytes, musician);

            // then
            mockedFiles.verify(() -> Files.write(any(Path.class), eq(pdfBytes), any(), any()));
            assertThat(filePath).isEqualTo(Paths.get(uploadDirectory, "Kowalski_agreement.pdf").toString());
        }
    }

    @Test
    void readFileAsBytes_whenFileExists_thenReturnsFileContent() {
        // given
        String path = "/test-uploads/test.pdf";
        byte[] content = "pdf content".getBytes();

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            mockedFiles.when(() -> Files.readAllBytes(any(Path.class))).thenReturn(content);

            // when
            byte[] result = fileStorageService.readFileAsBytes(path);

            // then
            assertThat(result).isEqualTo(content);
        }
    }

    @Test
    void readFileAsBytes_whenFileDoesNotExist_thenThrowsFileStorageException() {
        // given
        String path = "/test-uploads/nonexistent.pdf";

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            // when
            // then
            assertThatThrownBy(() -> fileStorageService.readFileAsBytes(path))
                    .isInstanceOf(FileStorageException.class)
                    .hasMessage("File not found at path: " + path);
        }
    }
}
