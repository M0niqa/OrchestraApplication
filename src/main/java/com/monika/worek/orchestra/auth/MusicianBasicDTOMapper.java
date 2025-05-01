package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.model.Musician;

public class MusicianBasicDTOMapper {
    public static MusicianBasicDTO mapToDto(Musician musician) {
        return new MusicianBasicDTO(
                musician.getId(),
                musician.getFirstName(),
                musician.getLastName(),
                musician.getInstrument(),
                musician.getPendingProjects(),
                musician.getAcceptedProjects(),
                musician.getRejectedProjects()
        );
    }
}
