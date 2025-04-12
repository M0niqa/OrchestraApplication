package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.MusicianDTO;
import com.monika.worek.orchestra.model.Musician;

public class MusicianDTOMapper {
    public static MusicianDTO map(Musician user) {
        return new MusicianDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles(),
                user.getBirthdate(),
                user.getAddress(),
                user.getPesel(),
                user.getTaxOffice(),
                user.getBankAccountNumber(),
                user.getInstrument(),
                user.getPendingProjects(),
                user.getAcceptedProjects(),
                user.getRefusedProjects()
        );
    }
}
