package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.exception.FileStorageException;
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
import java.util.List;
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
        AgreementTemplate template = getAgreementTemplate();

        musician = Musician.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .instrument(Instrument.VIOLIN_I)
                .address("123 Music Lane, Warsaw, Poland")
                .pesel("90010112345")
                .companyName("Company")
                .nip("543434343")
                .bankAccountNumber("PL12345678901234567890123456")
                .taxOffice(TaxOffice.US_WARSZAWA_MOKOTOW)
                .build();

        Map<String, BigDecimal> salaries = new HashMap<>();
        salaries.put("Strings", BigDecimal.valueOf(1000));

        project = Project.builder()
                .id(1L)
                .name("Grand Summer Gala")
                .location("National Philharmonic, Warsaw")
                .conductor("Agnieszka Duczmal")
                .programme("Symphony No. 7 'Seven Gates of Jerusalem'")
                .startDate(LocalDate.of(2025, 8, 1))
                .endDate(LocalDate.of(2025, 8, 10))
                .agreementTemplate(template)
                .groupSalaries(salaries)
                .build();
    }

    private static AgreementTemplate getAgreementTemplate() {
        AgreementTemplate template = new AgreementTemplate();
        template.setContent(
                "This agreement is made on ${agreementDate} for ${musician.fullName}, ${musician.companyName}, ${musician.address} (PESEL: ${musician.pesel}, nip ${musician.nip}). " +
                        "The project '${project.programme}', conducted by ${project.conductor}, will take place in ${project.location} from ${project.startDate} to ${project.endDate}. " +
                        "The net wage for playing ${musician.instrument} will be ${wageNet} PLN, payed to ${musician.bankAccountNumber}"
        );
        return template;
    }

    @Test
    void generateAgreementContent_whenGivenValidProjectAndMusician_shouldReturnSubstitutedContent() {
        // given
        // when
        String content = agreementService.generateAgreementContent(project, musician);

        // then
        assertThat(content).isEqualTo("This agreement is made on 2025-07-22 for John Smith, " +
                "Company, 123 Music Lane, Warsaw, Poland (PESEL: 90010112345, nip 543434343). " +
                "The project 'Symphony No. 7 'Seven Gates of Jerusalem'', conducted by Agnieszka Duczmal, " +
                "will take place in National Philharmonic, Warsaw from ${project.startDate} to 2025-08-10. " +
                "The net wage for playing Violin I will be 910.00 PLN, payed to PL12345678901234567890123456");
    }

    @Test
    void generateAgreementContent_whenProjectHasNoTemplate_shouldThrowException() {
        // given
        project.setAgreementTemplate(null);

        // when
        // then
        assertThatThrownBy(() -> agreementService.generateAgreementContent(project, musician))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not have an assigned template");
    }

    @Test
    void generateAgreementContent_whenProjectHasTemplateWithNullContent_shouldThrowException() {
        // given
        project.setAgreementTemplate(new AgreementTemplate());

        // when
        // then
        assertThatThrownBy(() -> agreementService.generateAgreementContent(project, musician))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("The assigned template '' is empty.");
    }

    @Test
    void generateAgreementContent_whenProjectHasTemplateWithEmptyContent_shouldThrowException() {
        // given
        project.setAgreementTemplate(AgreementTemplate.builder().content(" ").build());

        // when
        // then
        assertThatThrownBy(() -> agreementService.generateAgreementContent(project, musician))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("The assigned template '' is empty.");
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
        when(fileStorageService.saveGeneratedAgreement(any(), any())).thenReturn("/path/to/new.pdf");

        // when
        byte[] result = agreementService.getOrGenerateAgreement(1L, musician);

        // then
        assertThat(result).isEqualTo(newPdf);
        verify(musicianAgreementRepository, times(1)).save(any(MusicianAgreement.class));
    }

    @Test
    void getOrGenerateAgreement_whenDbRecordExistsButFileIsMissing_thenShouldDeleteRecordAndRegenerate() {
        // given
        byte[] newlyGeneratedPdf = "new pdf content".getBytes();
        MusicianAgreement staleAgreementRecord = new MusicianAgreement();
        staleAgreementRecord.setId(5L);
        staleAgreementRecord.setFilePath("/path/to/missing/file.pdf");

        when(projectService.getProjectById(1L)).thenReturn(project);
        when(musicianAgreementRepository.findByMusicianIdAndProjectId(1L, 1L)).thenReturn(Optional.of(staleAgreementRecord));
        when(fileStorageService.readFileAsBytes(staleAgreementRecord.getFilePath()))
                .thenThrow(new FileStorageException("File not found"));
        when(pdfService.generatePdfFromText(anyString())).thenReturn(newlyGeneratedPdf);
        when(fileStorageService.saveGeneratedAgreement(any(), any())).thenReturn("/path/to/newly/generated.pdf");

        // when
        byte[] result = agreementService.getOrGenerateAgreement(1L, musician);

        // then
        assertThat(result).isEqualTo(newlyGeneratedPdf);

        verify(musicianAgreementRepository, times(1)).delete(staleAgreementRecord);
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

    @Test
    void saveTemplate_whenCalled_thenShouldInvokeRepositorySave() {
        // given
        AgreementTemplate templateToSave = new AgreementTemplate();
        templateToSave.setContent("New template content");

        // when
        agreementService.saveTemplate(templateToSave);

        // then
        verify(agreementTemplateRepository, times(1)).save(templateToSave);
    }

    @Test
    void findAgreementsByProjectId_whenCalled_thenShouldReturnRepositoryResult() {
        // given
        Long projectId = 1L;
        List<MusicianAgreement> expectedAgreements = List.of(new MusicianAgreement(), new MusicianAgreement());
        when(musicianAgreementRepository.findByProjectId(projectId)).thenReturn(expectedAgreements);

        // when
        List<MusicianAgreement> actualAgreements = agreementService.findAgreementsByProjectId(projectId);

        // then
        assertThat(actualAgreements).isNotNull();
        assertThat(actualAgreements).hasSize(2);
        assertThat(actualAgreements).isEqualTo(expectedAgreements);
    }
}