package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.exception.MissingDataException;
import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.MusicianAgreement;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicianAgreementRepository;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.monika.worek.orchestra.calculator.DatesCalculator.*;
import static com.monika.worek.orchestra.calculator.WageCalculator.*;

@Service
public class AgreementGenerationService {

    private final MusicianService musicianService;
    private final ProjectService projectService;
    private final MusicianAgreementRepository musicianAgreementRepository;
    private final FileStorageService fileStorageService;
    private final PdfService pdfService;

    public AgreementGenerationService(MusicianService musicianService, ProjectService projectService, MusicianAgreementRepository musicianAgreementRepository, FileStorageService fileStorageService, PdfService pdfService) {
        this.musicianService = musicianService;
        this.projectService = projectService;
        this.musicianAgreementRepository = musicianAgreementRepository;
        this.fileStorageService = fileStorageService;
        this.pdfService = pdfService;
    }

    public String generateAgreementContent(Project project, Musician musician) {
        String templateContent = getTemplateContent(project);
        Map<String, String> valuesMap = prepareValueMap(project, musician);
        StringSubstitutor substitutor = new StringSubstitutor(valuesMap, "${", "}");

        return substitutor.replace(templateContent);
    }

    private static String getTemplateContent(Project project) {
        AgreementTemplate template = project.getAgreementTemplate();

        if (template == null) {
            throw new IllegalStateException("Cannot generate agreement: Project '" + project.getName() + "' does not have an assigned template.");
        }

        String templateContent = template.getContent();
        if (templateContent == null || templateContent.isBlank()) {
            throw new IllegalStateException("Cannot generate agreement: The assigned template '" + "' is empty.");
        }
        return templateContent;
    }

    private Map<String, String> prepareValueMap(Project project, Musician musician) {
        Map<String, String> valuesMap = new HashMap<>();
        String na = "N/A";

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        valuesMap.put("musician.fullName", String.format("%s %s",
                musician.getFirstName() != null ? musician.getFirstName() : na,
                musician.getLastName() != null ? musician.getLastName() : na).trim());
        valuesMap.put("musician.address", musician.getAddress() != null ? musician.getAddress() : na);
        valuesMap.put("musician.pesel", musician.getPesel() != null ? musician.getPesel() : na);
        valuesMap.put("musician.companyName", musician.getCompanyName() != null ? musician.getCompanyName() : na);
        valuesMap.put("musician.nip", musician.getNip() != null ? musician.getNip() : na);
        valuesMap.put("musician.bankAccountNumber", musician.getBankAccountNumber() != null ? musician.getBankAccountNumber() : na);
        valuesMap.put("musician.instrument", musician.getInstrument().toString());

        valuesMap.put("agreementDate", Objects.toString(getAgreementDate(project), na));
        valuesMap.put("project.endDate", project.getEndDate() != null ? project.getEndDate().format(dateFormatter) : na);
        valuesMap.put("project.location", Objects.toString(project.getLocation(), na));
        valuesMap.put("project.conductor", project.getConductor() != null ? project.getConductor() : na);
        valuesMap.put("project.programme", Objects.toString(project.getProgramme(), na));
        valuesMap.put("paymentDeadline", Objects.toString(getPaymentDeadline(project), na));
        valuesMap.put("resignationPenaltyDate", Objects.toString(getResignationPenaltyDate(project), na));
        valuesMap.put("wage", Objects.toString(getGrossWage(project, musician), na));
        valuesMap.put("wageNet", Objects.toString(getNetWage(project, musician), na));
        valuesMap.put("invoiceData", Objects.toString(getInvoiceDate(project), na));
        valuesMap.put("costOfIncome", Objects.toString(getCostOfIncome(project, musician), na));
        valuesMap.put("tax", Objects.toString(getTax(project, musician), na));

        return valuesMap;
    }

    public byte[] getOrGenerateAgreement(Long projectId, Musician musician) {
        if (musicianService.isDataMissing(musician)) {
            throw new MissingDataException("Your personal or business data is incomplete.");
        }

        Project project = projectService.getProjectById(projectId);

        return musicianAgreementRepository.findByMusicianIdAndProjectId(projectId, musician.getId())
                .map(a -> fileStorageService.readFileAsBytes(a.getFilePath()))
                .orElseGet(() -> generateAndStoreAgreement(project, musician));
    }

    private byte[] generateAndStoreAgreement(Project project, Musician musician) {
        String content = generateAgreementContent(project, musician);

        byte[] pdf = pdfService.generatePdfFromText(content);
        String path = fileStorageService.saveGeneratedAgreement(pdf, musician, project);

        MusicianAgreement agreement = new MusicianAgreement();
        agreement.setFileName(musician.getLastName() + "_agreement.pdf");
        agreement.setFilePath(path);
        agreement.setFileType("application/pdf");
        agreement.setCreatedAt(LocalDateTime.now());
        agreement.setMusician(musician);
        agreement.setProject(project);

        musicianAgreementRepository.save(agreement);

        return pdf;
    }
}