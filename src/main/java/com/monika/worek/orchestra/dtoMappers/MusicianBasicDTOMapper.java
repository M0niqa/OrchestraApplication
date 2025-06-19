package com.monika.worek.orchestra.dtoMappers;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.model.Musician;

public class MusicianBasicDTOMapper {
    public static MusicianBasicDTO mapToDto(Musician musician) {
        if (musician == null) {
            return null;
        }
        return new MusicianBasicDTO(
                musician.getId(),
                musician.getFirstName(),
                musician.getLastName(),
                musician.getInstrument()
        );
    }
}
