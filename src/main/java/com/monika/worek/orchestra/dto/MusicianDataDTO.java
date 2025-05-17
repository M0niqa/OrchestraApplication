package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.TaxOffice;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import org.hibernate.validator.constraints.pl.NIP;
import org.hibernate.validator.constraints.pl.PESEL;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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
    private String companyName;
    @NotNull(message = "Tax office must be selected")
    private TaxOffice taxOffice;
    @NotBlank(message = "Bank account number is required")
    private String bankAccountNumber;

    public MusicianDataDTO(String firstName, String lastName, String email, LocalDate birthdate, String address, String pesel, String companyName, String nip, TaxOffice taxOffice, String bankAccountNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.address = address;
        this.pesel = pesel;
        this.companyName = companyName;
        this.nip = nip;
        this.taxOffice = taxOffice;
        this.bankAccountNumber = bankAccountNumber;
    }
}
