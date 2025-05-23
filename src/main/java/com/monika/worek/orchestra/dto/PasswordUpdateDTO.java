package com.monika.worek.orchestra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordUpdateDTO {

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d])[A-Za-z\\d\\S]{8,}$",
            message = "Password must be at least 8 characters long, including upper and lower case letters, a number, and a special character")
    private String newPassword;

    @NotBlank(message = "Please confirm the new password")
    private String confirmNewPassword;
}
