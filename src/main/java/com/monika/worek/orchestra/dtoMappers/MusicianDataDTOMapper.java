package com.monika.worek.orchestra.dtoMappers;

import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.model.Musician;

public class MusicianDataDTOMapper {
    public static MusicianDataDTO mapToDto(Musician user) {
        return new MusicianDataDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBirthdate(),
                user.getAddress(),
                maskPesel(user.getPesel()),
                user.getCompanyName(),
                user.getNip(),
                user.getTaxOffice(),
                user.getBankAccountNumber()
        );
    }

    private static String maskPesel(String pesel) {
        if (pesel != null && !pesel.isBlank()) {
            return "*******" + pesel.substring(pesel.length()-2);
        }
        return "";
    }
}
