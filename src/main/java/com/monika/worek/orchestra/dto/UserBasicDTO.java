package com.monika.worek.orchestra.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserBasicDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}
