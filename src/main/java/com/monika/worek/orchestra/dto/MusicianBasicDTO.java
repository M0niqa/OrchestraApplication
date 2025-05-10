package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Instrument;

public record MusicianBasicDTO(Long id, String firstName, String lastName, Instrument instrument) {

}