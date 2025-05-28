package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.exception.MissingDataException;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.service.AgreementService;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.PdfService;
import com.monika.worek.orchestra.service.ProjectService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MusicianAgreementController {

    private final AgreementService agreementService;
    private final MusicianService musicianService;
    private final ProjectService projectService;

    public MusicianAgreementController(AgreementService agreementService, MusicianService musicianService, PdfService pdfService, ProjectService projectService, ProjectService projectService1) {
        this.agreementService = agreementService;
        this.musicianService = musicianService;
        this.projectService = projectService1;
    }

    @GetMapping("/musician/project/{projectId}/downloadAgreement")
    public ResponseEntity<byte[]> downloadAgreement(@PathVariable Long projectId, Authentication authentication) {
        Musician musician = musicianService.getMusicianByEmail(authentication.getName());
        projectService.throwIfUnauthorized(projectId, authentication.getName());
        try {
            byte[] agreement = agreementService.getOrGenerateAgreement(projectId, musician);

            String filename = musician.getLastName() + "_agreement.pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(agreement);

        } catch (MissingDataException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to generate agreement".getBytes());
        }
    }
}
