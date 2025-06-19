package com.monika.worek.orchestra.mapper;

import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.dtoMappers.MusicianDataDTOMapper;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.TaxOffice;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MusicianDataDTOMapperTest {

    @Test
    void mapToDto_whenGivenNullMusician_thenShouldReturnNull() {
        // given
        Musician musician = null;

        // when
        MusicianDataDTO dto = MusicianDataDTOMapper.mapToDto(musician);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void mapToDto_whenMusicianHasNullPesel_thenShouldReturnEmptyStringForPesel() {
        // given
        Musician musician = new Musician();
        musician.setPesel(null);

        // when
        MusicianDataDTO dto = MusicianDataDTOMapper.mapToDto(musician);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getPesel()).isEmpty();
    }

    @Test
    void mapToDto_whenMusicianHasBlankPesel_thenShouldReturnEmptyStringForPesel() {
        // given
        Musician musician = new Musician();
        musician.setPesel("   ");

        // when
        MusicianDataDTO dto = MusicianDataDTOMapper.mapToDto(musician);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getPesel()).isEmpty();
    }

    @Test
    void mapToDto_whenGivenValidMusician_thenShouldMapAllFieldsAndMaskedPesel() {
        // given
        Musician musician = buildMusician();

        // when
        MusicianDataDTO dto = MusicianDataDTOMapper.mapToDto(musician);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getFirstName()).isEqualTo("Monika");
        assertThat(dto.getLastName()).isEqualTo("Nowak");
        assertThat(dto.getEmail()).isEqualTo("monika@example.com");
        assertThat(dto.getBirthdate()).isEqualTo(LocalDate.of(1992, 8, 22));
        assertThat(dto.getAddress()).isEqualTo("os. Wysokie 9, 31-804 Kraków");
        assertThat(dto.getPesel()).isEqualTo("*******45");
        assertThat(dto.getCompanyName()).isEqualTo("Music Inc.");
        assertThat(dto.getNip()).isEqualTo("1234567890");
        assertThat(dto.getTaxOffice()).isEqualTo(TaxOffice.US_KRAKOW_NOWA_HUTA);
        assertThat(dto.getBankAccountNumber()).isEqualTo("98765432109876543210987654");
    }

    private static Musician buildMusician() {
        Musician musician = new Musician();
        musician.setFirstName("Monika");
        musician.setLastName("Nowak");
        musician.setEmail("monika@example.com");
        musician.setBirthdate(LocalDate.of(1992, 8, 22));
        musician.setAddress("os. Wysokie 9, 31-804 Kraków");
        musician.setPesel("92082212345");
        musician.setCompanyName("Music Inc.");
        musician.setNip("1234567890");
        musician.setTaxOffice(TaxOffice.US_KRAKOW_NOWA_HUTA);
        musician.setBankAccountNumber("98765432109876543210987654");
        return musician;
    }
}