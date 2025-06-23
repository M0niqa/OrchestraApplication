package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.constraint.PESEL;
import com.monika.worek.orchestra.constraint.nip;
import com.monika.worek.orchestra.model.TaxOffice;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MusicianDataDTO {

    @NotBlank(message = "Name is required")
    private String firstName;
    @NotBlank(message = "Surname is required")
    private String lastName;
    @NotBlank(message = "Mail is required")
    @Email(message = "Invalid email address")
    private String email;
    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthdate;
    @NotBlank(message = "Address is required")
    private String address;
    @PESEL(message = "Invalid Polish National Identification Number (PESEL)")
    private String pesel;
    @nip(message = "Invalid VAT Identification Number")
    private String nip;
    private String companyName;
    @NotNull(message = "Tax office must be selected")
    private TaxOffice taxOffice;
    @NotBlank(message = "Bank account number is required")
    private String bankAccountNumber;
}
