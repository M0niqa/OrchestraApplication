package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UserLoginDTO {
    private String email;
    private String password;
    private Set<UserRole> roles;
}
