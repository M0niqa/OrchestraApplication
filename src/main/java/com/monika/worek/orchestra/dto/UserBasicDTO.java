package com.monika.worek.orchestra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicDTO {
    private Long id;
    private String firstName;
    private String lastName;
}
