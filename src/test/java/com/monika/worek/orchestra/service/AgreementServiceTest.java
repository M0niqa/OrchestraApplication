package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.exception.MissingDataException;
import com.monika.worek.orchestra.model.*;
import com.monika.worek.orchestra.repository.AgreementTemplateRepository;
import com.monika.worek.orchestra.repository.MusicianAgreementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AgreementServiceTest {

    @Mock
    private MusicianService musicianService;
    @Mock
    private ProjectService projectService;
    @Mock
    private MusicianAgreementRepository musicianAgreementRepository;
    @Mock
    private AgreementTemplateRepository agreementTemplateRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private PdfService pdfService;


    @InjectMocks
    private AgreementService agreementService;

    private Project project;
    private Musician musician;

    @BeforeEach
    void setUp() {
        AgreementTemplate template = new AgreementTemplate();
        template.setContent(
                "This agreement is made on ${agreementDate} for ${musician.fullName} (PESEL: ${musician.pesel}). " +
                        "The project '${project.programme}', conducted by ${project.conductor}, will take place in ${project.location}. " +
                        "The net wage for playing ${musician.instrument} will be ${wageNet} PLN."
        );

        musician = Musician.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .instrument(Instrument.VIOLIN_I)
                .address("123 Music Lane, Warsaw, Poland")
                .pesel("90010112345")
                .companyName(null)
                .nip(null)
                .bankAccountNumber("PL12345678901234567890123456")
                .taxOffice(TaxOffice.US_WARSZAWA_MOKOTOW)
                .build();

        Map<String, BigDecimal> salaries = new HashMap<>();
        salaries.put("Strings", BigDecimal.valueOf(1000));

        project = Project.builder()
                .id(1L)
                .name("Grand Summer Gala")
                .location("National Philharmonic, Warsaw")
                .conductor("Krzysztof Penderecki")
                .programme("Symphony No. 7 'Seven Gates of Jerusalem'")
                .startDate(LocalDate.of(2025, 8, 1))
                .endDate(LocalDate.of(2025, 8, 10))
                .agreementTemplate(template)
                .groupSalaries(salaries)
                .build();
    }

    @Test
    void generateAgreementContent_whenGivenValidProjectAndMusician_shouldReturnSubstitutedContent() {
        // given
        // when
        String content = agreementService.generateAgreementContent(project, musician);

        // then
        assertThat(content).isEqualTo("This agreement is made on 2025-07-22 for John Smith (PESEL: 90010112345). " +
                "The project 'Symphony No. 7 'Seven Gates of Jerusalem'', conducted by Krzysztof Penderecki, will take place in National Philharmonic, Warsaw. " +
                "The net wage for playing Violin I will be 910.00 PLN.");
    }

    @Test
    void generateAgreementContent_whenProjectHasNoTemplate_shouldThrowException() {
        // givenN/A
        project.setAgreementTemplate(null);

        // when
        // then
        assertThatThrownBy(() -> agreementService.generateAgreementContent(project, musician))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not have an assigned template");
    }

    @Test
    void getOrGenerateAgreement_whenMusicianDataIsMissing_shouldThrowException() {
        // given
        when(musicianService.isDataMissing(musician)).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> agreementService.getOrGenerateAgreement(1L, musician))
                .isInstanceOf(MissingDataException.class)
                .hasMessage("Your personal or business data is incomplete.");
    }

    @Test
    void getOrGenerateAgreement_whenAgreementAlreadyExists_shouldReturnExistingFileBytes() {
        // given
        byte[] existingPdf = "existing pdf".getBytes();
        MusicianAgreement existingAgreement = new MusicianAgreement();
        existingAgreement.setFilePath("/path/to/existing.pdf");

        when(musicianService.isDataMissing(musician)).thenReturn(false);
        when(projectService.getProjectById(1L)).thenReturn(project);
        when(musicianAgreementRepository.findByMusicianIdAndProjectId(1L, 1L))
                .thenReturn(Optional.of(existingAgreement));
        when(fileStorageService.readFileAsBytes("/path/to/existing.pdf")).thenReturn(existingPdf);

        // when
        byte[] result = agreementService.getOrGenerateAgreement(1L, musician);

        // then
        assertThat(result).isEqualTo(existingPdf);
        verify(pdfService, never()).generatePdfFromText(any());
    }

    @Test
    void getOrGenerateAgreement_whenAgreementDoesNotExist_shouldGenerateAndStoreNewAgreement() {
        // given
        byte[] newPdf = "new pdf".getBytes();
        when(musicianService.isDataMissing(musician)).thenReturn(false);
        when(projectService.getProjectById(1L)).thenReturn(project);
        when(musicianAgreementRepository.findByMusicianIdAndProjectId(1L, 1L))
                .thenReturn(Optional.empty());
        when(pdfService.generatePdfFromText(anyString())).thenReturn(newPdf);
        when(fileStorageService.saveGeneratedAgreement(any(), any(), any())).thenReturn("/path/to/new.pdf");

        // when
        byte[] result = agreementService.getOrGenerateAgreement(1L, musician);

        // then
        assertThat(result).isEqualTo(newPdf);
        verify(musicianAgreementRepository, times(1)).save(any(MusicianAgreement.class));
    }

    @Test
    void findTemplateById_whenTemplateNotFound_shouldThrowException() {
        // given
        when(agreementTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> agreementService.findTemplateById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Template not found");
    }
}