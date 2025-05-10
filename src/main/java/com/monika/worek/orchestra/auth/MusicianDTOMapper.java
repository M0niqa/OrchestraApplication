package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.model.Musician;

public class MusicianDTOMapper {
    public static MusicianDataDTO mapToDto(Musician user) {
        return new MusicianDataDTO(
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
                user.getInstrument()
        );
    }
}
