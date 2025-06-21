package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.TaxOffice;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicianServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MusicianRepository musicianRepository;

    @InjectMocks
    private MusicianService musicianService;

    @Test
    void getMusicianByEmail_whenMusicianExists_thenShouldReturnMusician() {
        // given
        String email = "test@example.com";
        Musician expectedMusician = Musician.builder().email(email).build();
        when(musicianRepository.findByEmail(email)).thenReturn(Optional.of(expectedMusician));

        // when
        Musician actualMusician = musicianService.getMusicianByEmail(email);

        // then
        assertThat(actualMusician).isEqualTo(expectedMusician);
    }

    @Test
    void getMusicianByEmail_whenMusicianDoesNotExist_thenShouldThrowException() {
        // given
        String email = "nonexistent@example.com";
        when(musicianRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> musicianService.getMusicianByEmail(email))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Musician not found");
    }

    @Test
    void getAllMusiciansSortedBySurname_whenCalled_thenShouldReturnSortedList() {
        // given
        Musician musicianB = Musician.builder().lastName("Brown").firstName("Michael").build();
        Musician musicianA = Musician.builder().lastName("Adamczyk").firstName("Patrycja").build();
        when(musicianRepository.findAll()).thenReturn(List.of(musicianB, musicianA));

        // when
        List<Musician> sortedMusicians = musicianService.getAllMusiciansSortedBySurname();

        // then
        assertThat(sortedMusicians).isNotNull();
        assertThat(sortedMusicians).hasSize(2);
        assertThat(sortedMusicians).containsExactly(musicianA, musicianB);
    }

    @Test
    void updateMusicianData_whenMusicianExists_thenShouldUpdateAllFields() {
        // given
        String currentEmail = "original@example.com";

        Musician existingMusician = Musician.builder()
                .email(currentEmail)
                .firstName("OriginalFirst")
                .lastName("OriginalLast")
                .birthdate(LocalDate.of(1990, 1, 1))
                .address("Old Address 1")
                .pesel("90010111111")
                .nip("653395997")
                .companyName("Company Name")
                .bankAccountNumber("111111111")
                .taxOffice(TaxOffice.US_KRAKOW_PODGORZE)
                .build();

        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.of(existingMusician));

        MusicianDataDTO newData = createMusicianDataDTO();

        // when
        musicianService.updateMusicianData(currentEmail, newData);

        // then
        assertThat(existingMusician.getFirstName()).isEqualTo("UpdatedFirst");
        assertThat(existingMusician.getLastName()).isEqualTo("UpdatedLast");
        assertThat(existingMusician.getEmail()).isEqualTo("new@example.com");
        assertThat(existingMusician.getBirthdate()).isEqualTo(LocalDate.of(1995, 5, 5));
        assertThat(existingMusician.getAddress()).isEqualTo("New Address 123");
        assertThat(existingMusician.getPesel()).isEqualTo("95050555555");
        assertThat(existingMusician.getNip()).isEqualTo("124566767");
        assertThat(existingMusician.getCompanyName()).isEqualTo("New Company");
        assertThat(existingMusician.getBankAccountNumber()).isEqualTo("222222222");
        assertThat(existingMusician.getTaxOffice()).isEqualTo(TaxOffice.US_WARSZAWA_MOKOTOW);
    }

    private static MusicianDataDTO createMusicianDataDTO() {
        MusicianDataDTO newData = new MusicianDataDTO();
        newData.setFirstName("UpdatedFirst");
        newData.setLastName("UpdatedLast");
        newData.setEmail("new@example.com");
        newData.setBirthdate(LocalDate.of(1995, 5, 5));
        newData.setAddress("New Address 123");
        newData.setPesel("95050555555");
        newData.setNip("124566767");
        newData.setCompanyName("New Company");
        newData.setBankAccountNumber("222222222");
        newData.setTaxOffice(TaxOffice.US_WARSZAWA_MOKOTOW);
        return newData;
    }

    @Test
    void updateMusicianData_whenMusicianDoesNotExist_thenShouldThrowException() {
        // given
        String nonExistentEmail = "ghost@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        MusicianDataDTO newData = new MusicianDataDTO();

        // when
        // then
        assertThatThrownBy(() -> musicianService.updateMusicianData(nonExistentEmail, newData))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Musician not found");
    }

    @Test
    void isDataMissing_whenBothPersonalAndBusinessDataAreMissing_thenShouldReturnTrue() {
        // given
        Musician musician = new Musician();

        // when
        boolean isMissing = musicianService.isDataMissing(musician);

        // then
        assertThat(isMissing).isTrue();
    }

    @Test
    void isDataMissing_whenPersonalDataIsComplete_thenShouldReturnFalse() {
        // given
        Musician musician = Musician.builder()
                .firstName("John")
                .lastName("Smith")
                .address("123 Main St")
                .pesel("12345678901")
                .bankAccountNumber("12345")
                .build();

        // when
        boolean isMissing = musicianService.isDataMissing(musician);

        // then
        assertThat(isMissing).isFalse();
    }

    @Test
    void isDataMissing_whenBusinessDataIsComplete_thenShouldReturnFalse() {
        // given
        Musician musician = Musician.builder()
                .companyName("Prestige MJM")
                .nip("1234567890")
                .bankAccountNumber("12345")
                .build();

        // when
        boolean isMissing = musicianService.isDataMissing(musician);

        // then
        assertThat(isMissing).isFalse();
    }

    @Test
    void isDataMissing_whenPersonalDataIsIncomplete_thenShouldReturnTrue() {
        // given
        Musician musician = Musician.builder()
                .firstName("John")
                .lastName("Smith")
                .address("123 Main St")
                .pesel(null)
                .bankAccountNumber("12345")
                .build();

        // when
        boolean isMissing = musicianService.isDataMissing(musician);

        // then
        assertThat(isMissing).isTrue();
    }
}
