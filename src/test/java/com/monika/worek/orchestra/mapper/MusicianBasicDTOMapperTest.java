package com.monika.worek.orchestra.mapper;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dtoMappers.MusicianBasicDTOMapper;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MusicianBasicDTOMapperTest {

    @Test
    void mapToDto_whenGivenNullEntity_thenShouldReturnNull() {
        // given
        Musician musician = null;

        // when
        MusicianBasicDTO dto = MusicianBasicDTOMapper.mapToDto(musician);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void mapToDto_whenEntityFieldIsNull_thenDtoFieldShouldAlsoBeNull() {
        // given
        Musician musicianWithNullField = new Musician();
        musicianWithNullField.setId(1L);
        musicianWithNullField.setFirstName(null);
        musicianWithNullField.setLastName("Smith");
        musicianWithNullField.setInstrument(Instrument.VIOLIN_I);

        // when
        MusicianBasicDTO dto = MusicianBasicDTOMapper.mapToDto(musicianWithNullField);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.firstName()).isNull();
        assertThat(dto.lastName()).isEqualTo("Smith");
        assertThat(dto.instrument()).isEqualTo(Instrument.VIOLIN_I);
    }

    @Test
    void mapToDto_whenGivenValidEntity_thenShouldReturnDtoWithAllFields() {
        // given
        Musician musician = new Musician();
        musician.setId(1L);
        musician.setFirstName("Anna");
        musician.setLastName("Smith");
        musician.setInstrument(Instrument.VIOLIN_I);

        // when
        MusicianBasicDTO dto = MusicianBasicDTOMapper.mapToDto(musician);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(musician.getId());
        assertThat(dto.firstName()).isEqualTo(musician.getFirstName());
        assertThat(dto.lastName()).isEqualTo(musician.getLastName());
        assertThat(dto.instrument()).isEqualTo(musician.getInstrument());
    }
}
