package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.Set;

@Getter
public class UserDTO {

    private Long id;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private String email;
    private String password;
    private Set<UserRole> roles;

    public UserDTO(Long id, String firstName, String lastName, String email, String password, Set<UserRole> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
