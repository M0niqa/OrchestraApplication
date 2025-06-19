package com.monika.worek.orchestra.dtoMappers;

import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.model.Musician;

public class MusicianDataDTOMapper {
    public static MusicianDataDTO mapToDto(Musician musician) {
        if (musician == null) {
            return null;
        }
        return new MusicianDataDTO(
                musician.getFirstName(),
                musician.getLastName(),
                musician.getEmail(),
                musician.getBirthdate(),
                musician.getAddress(),
                maskPesel(musician.getPesel()),
                musician.getNip(),
                musician.getCompanyName(),
                musician.getTaxOffice(),
                musician.getBankAccountNumber()
        );
    }

    private static String maskPesel(String pesel) {
        if (pesel != null && !pesel.isBlank()) {
            return "*******" + pesel.substring(pesel.length()-2);
        }
        return "";
    }
}
