package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.TaxOffice;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.pl.NIP;
import org.hibernate.validator.constraints.pl.PESEL;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
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
    @PESEL(message = "Invalid Polish national identification number (PESEL)")
    private String pesel;
    @NIP
    private String nip;
    private final String companyName;
    @NotNull(message = "Tax office must be selected")
    private TaxOffice taxOffice;
    @NotBlank(message = "Bank account number is required")
    private String bankAccountNumber;
}
