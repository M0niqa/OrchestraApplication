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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAgreementController.class)
@WithMockUser(username = "admin@example.com", roles = "ADMIN")
class AdminAgreementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfService pdfService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private AgreementService agreementService;

    @Test
    void editTemplateForm_whenGetRequest_thenReturnsEditTemplateViewWithContent() throws Exception {
        // given
        String templateContent = "This is the agreement template content.";
        AgreementTemplate template = new AgreementTemplate();
        template.setContent(templateContent);
        when(agreementService.findTemplateById(1L)).thenReturn(template);

        // when
        // then
        mockMvc.perform(get("/admin/template/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin-edit-template"))
                .andExpect(model().attribute("templateContent", templateContent));
    }

    @Test
    void updateTemplate_whenPostRequest_thenUpdatesTemplateAndRedirects() throws Exception {
        // given
        String newContent = "This is the updated content.";
        AgreementTemplate existingTemplate = new AgreementTemplate();
        when(agreementService.findTemplateById(1L)).thenReturn(existingTemplate);

        // when
        // then
        mockMvc.perform(post("/admin/template/edit")
                        .param("templateContent", newContent)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/template/edit"))
                .andExpect(flash().attribute("success", "Template updated successfully."));

        verify(agreementService, times(1)).saveTemplate(any(AgreementTemplate.class));
    }

    @Test
    void downloadAllAgreements_whenAgreementsExist_thenReturnsMergedPdf() throws Exception {
        // given
        Project project = new Project();
        Musician musician = Musician.builder().id(1L).build();
        project.setProjectMembers(Set.of(musician));
        MusicianAgreement agreement = MusicianAgreement.builder().musician(musician).build();
        agreement.setFilePath("test/path/agreement.pdf");
        byte[] pdfBytes = "PDF content".getBytes();

        when(projectService.getProjectById(1L)).thenReturn(project);
        when(agreementService.findAgreementsByProjectId(1L)).thenReturn(List.of(agreement));
        when(pdfService.mergePdfFiles(anyList())).thenReturn(pdfBytes);

        // when
        // then
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

        // when
        // then
        mockMvc.perform(get("/admin/project/1/downloadAgreements"))
                .andExpect(status().isNoContent());
    }

    @Test
    void downloadAllAgreements_whenPdfMergeFails_thenReturnsInternalServerError() throws Exception {
        // given
        Project project = new Project();
        Musician musician = Musician.builder().id(1L).build();
        project.setProjectMembers(Set.of(musician));
        MusicianAgreement agreement = MusicianAgreement.builder().musician(musician).build();
        agreement.setFilePath("dummy/path/agreement.pdf");

        when(projectService.getProjectById(1L)).thenReturn(project);
        when(agreementService.findAgreementsByProjectId(1L)).thenReturn(List.of(agreement));
        when(pdfService.mergePdfFiles(anyList())).thenThrow(new IOException("Disk is full"));

        // when
        // then
        mockMvc.perform(get("/admin/project/1/downloadAgreements"))
                .andExpect(status().isInternalServerError());
    }
}
