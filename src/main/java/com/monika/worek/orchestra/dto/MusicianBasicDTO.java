package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Instrument;
import lombok.Getter;

@Getter
public class MusicianBasicDTO extends UserBasicDTO {

    private final Instrument instrument;

    public MusicianBasicDTO(Long id, String firstName, String lastName, Instrument instrument) {
        super(id, firstName, lastName);
        this.instrument = instrument;
    }
}