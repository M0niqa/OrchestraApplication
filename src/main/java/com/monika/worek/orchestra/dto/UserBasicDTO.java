package com.monika.worek.orchestra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBasicDTO {
    private Long id;
    private String firstName;
    private String lastName;
}
