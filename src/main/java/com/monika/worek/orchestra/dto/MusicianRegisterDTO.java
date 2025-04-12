package com.monika.worek.orchestra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MusicianRegisterDTO {

    private String email;
    private String firstName;
    private String lastName;
}
