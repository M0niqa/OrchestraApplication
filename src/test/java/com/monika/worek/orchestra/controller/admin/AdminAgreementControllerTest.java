package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.MusicianAgreement;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.AgreementService;
import com.monika.worek.orchestra.service.PdfService;
import com.monika.worek.orchestra.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAgreementController.class)
class AdminAgreementControllerTest {

    // MockMvc allows you to send HTTP requests to your controller
    @Autowired
    private MockMvc mockMvc;

    // Use @MockBean to create mock versions of the service dependencies
    @MockBean
    private PdfService pdfService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private AgreementService agreementService;

    // --- Tests for editTemplateForm() ---
    @Test
    void editTemplateForm_whenGetRequest_thenReturnsEditTemplateViewWithContent() throws Exception {
        // given
        String templateContent = "This is the agreement template content.";
        AgreementTemplate template = new AgreementTemplate();
        template.setContent(templateContent);
        when(agreementService.findTemplateById(1L)).thenReturn(template);

        // when & then
        mockMvc.perform(get("/admin/template/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin-edit-template"))
                .andExpect(model().attribute("templateContent", templateContent));
    }

    // --- Tests for updateTemplate() ---
    @Test
    void updateTemplate_whenPostRequest_thenUpdatesTemplateAndRedirects() throws Exception {
        // given
        String newContent = "This is the updated content.";
        AgreementTemplate existingTemplate = new AgreementTemplate();
        when(agreementService.findTemplateById(1L)).thenReturn(existingTemplate);

        // when & then
        mockMvc.perform(post("/admin/template/edit")
                        .param("templateContent", newContent))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/template/edit"))
                .andExpect(flash().attribute("success", "Template updated successfully."));

        // Verify that the save method was called on the service
        verify(agreementService, times(1)).saveTemplate(any(AgreementTemplate.class));
    }

    // --- Tests for downloadAllAgreements() ---
    @Test
    void downloadAllAgreements_whenAgreementsExist_thenReturnsMergedPdf() throws Exception {
        // given
        Project project = new Project();
        project.setProjectMembers(Collections.singleton(new Musician())); // Add at least one member
        MusicianAgreement agreement = new MusicianAgreement();
        agreement.setFilePath("dummy/path/agreement.pdf");
        byte[] pdfBytes = "PDF content".getBytes();

        when(projectService.getProjectById(1L)).thenReturn(project);
        when(agreementService.findAgreementsByProjectId(1L)).thenReturn(List.of(agreement));
        when(pdfService.mergePdfFiles(anyList())).thenReturn(pdfBytes);

        // when & then
        mockMvc.perform(get("/admin/project/1/downloadAgreements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=all_agreements.pdf"))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void downloadAllAgreements_whenNoAgreementsExist_thenReturnsNoContent() throws Exception {
        // given
        Project project = new Project();
        project.setProjectMembers(Collections.emptySet());
        when(projectService.getProjectById(1L)).thenReturn(project);
        when(agreementService.findAgreementsByProjectId(1L)).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/admin/project/1/downloadAgreements"))
                .andExpect(status().isNoContent());
    }

    @Test
    void downloadAllAgreements_whenPdfMergeFails_thenReturnsInternalServerError() throws Exception {
        // given
        Project project = new Project();
        project.setProjectMembers(Collections.singleton(new Musician()));
        MusicianAgreement agreement = new MusicianAgreement();
        agreement.setFilePath("dummy/path/agreement.pdf");

        when(projectService.getProjectById(1L)).thenReturn(project);
        when(agreementService.findAgreementsByProjectId(1L)).thenReturn(List.of(agreement));
        // Simulate an IOException during the PDF merge
        when(pdfService.mergePdfFiles(anyList())).thenThrow(new IOException("Disk is full"));

        // when & then
        mockMvc.perform(get("/admin/project/1/downloadAgreements"))
                .andExpect(status().isInternalServerError());
    }
}
