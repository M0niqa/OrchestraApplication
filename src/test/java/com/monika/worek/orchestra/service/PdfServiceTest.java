package com.monika.worek.orchestra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PdfServiceTest {

    private PdfService pdfService;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        pdfService = new PdfService();
    }

    @Test
    void generatePdfFromText_whenGivenValidContent_thenShouldReturnPdfByteArray() {
        // given
        String content = "This is a test PDF.";

        // when
        byte[] pdfBytes = pdfService.generatePdfFromText(content);

        // then
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    void generatePdfFromText_whenGivenEmptyContent_thenShouldThrowException() {
        // given
        String content = "";

        // when
        // then
        assertThatThrownBy(() -> pdfService.generatePdfFromText(content))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void mergePdfFiles_whenGivenMultipleFiles_thenShouldReturnMergedPdf() throws IOException {
        // given
        File file1 = createTestPdfFile("content1");
        File file2 = createTestPdfFile("content2");
        List<File> filesToMerge = List.of(file1, file2);

        // when
        byte[] mergedBytes = pdfService.mergePdfFiles(filesToMerge);

        // then
        assertThat(mergedBytes).isNotNull();
        assertThat(mergedBytes.length).isGreaterThan((int) file1.length());
        assertThat(mergedBytes.length).isGreaterThan((int) file2.length());
    }

    @Test
    void mergePdfFiles_whenGivenEmptyList_thenShouldReturnEmptyPdf() throws IOException {
        // given
        List<File> emptyList = Collections.emptyList();

        // when
        byte[] mergedBytes = pdfService.mergePdfFiles(emptyList);

        // then
        assertThat(mergedBytes).isNotNull();
    }

    @Test
    void mergePdfFiles_whenFileDoesNotExist_thenShouldThrowIOException() {
        // given
        File nonExistentFile = new File(tempDir.toFile(), "nonexistent.pdf");
        List<File> filesToMerge = List.of(nonExistentFile);

        // when
        // then
        assertThatThrownBy(() -> pdfService.mergePdfFiles(filesToMerge))
                .isInstanceOf(IOException.class);
    }


    private File createTestPdfFile(String content) throws IOException {
        Path filePath = tempDir.resolve(content + "_test.pdf");
        byte[] pdfBytes = pdfService.generatePdfFromText(content);
        Files.write(filePath, pdfBytes);
        return filePath.toFile();
    }
}