package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.exception.MissingDataException;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.service.AgreementService;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MusicianAgreementController.class)
@WithMockUser(username = "musician@example.com", roles = "MUSICIAN")
class MusicianAgreementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgreementService agreementService;

    @MockBean
    private MusicianService musicianService;

    @MockBean
    private ProjectService projectService;

    private Musician musician;

    @BeforeEach
    void setUp() {
        musician = Musician.builder()
                .id(1L)
                .email("musician@example.com")
                .lastName("Smith")
                .build();
    }

    @Test
    void downloadAgreement_whenAgreementIsGeneratedSuccessfully_thenShouldReturnPdfFile() throws Exception {
        // given
        Long projectId = 1L;
        byte[] pdfContent = "This is a PDF".getBytes();

        when(musicianService.getMusicianByEmail("musician@example.com")).thenReturn(musician);
        doNothing().when(projectService).throwIfUnauthorized(projectId, "musician@example.com");
        when(agreementService.getOrGenerateAgreement(projectId, musician)).thenReturn(pdfContent);

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/downloadAgreement", projectId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=Smith_agreement.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfContent));
    }

    @Test
    void downloadAgreement_whenMusicianDataIsMissing_thenShouldReturnForbiddenStatus() throws Exception {
        // given
        Long projectId = 1L;
        String errorMessage = "Your personal or business data is incomplete.";

        when(musicianService.getMusicianByEmail("musician@example.com")).thenReturn(musician);
        doNothing().when(projectService).throwIfUnauthorized(projectId, "musician@example.com");
        when(agreementService.getOrGenerateAgreement(projectId, musician)).thenThrow(new MissingDataException(errorMessage));

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/downloadAgreement", projectId))
                .andExpect(status().isForbidden())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void downloadAgreement_whenGenericExceptionOccurs_thenShouldReturnInternalServerError() throws Exception {
        // given
        Long projectId = 1L;
        when(musicianService.getMusicianByEmail("musician@example.com")).thenReturn(musician);
        doNothing().when(projectService).throwIfUnauthorized(projectId, "musician@example.com");
        when(agreementService.getOrGenerateAgreement(projectId, musician)).thenThrow(new RuntimeException("Something went wrong"));

        // when
        // then
        mockMvc.perform(get("/musician/project/{projectId}/downloadAgreement", projectId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to generate agreement"));
    }
}