package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.TaxOffice;
import com.monika.worek.orchestra.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import org.hibernate.validator.constraints.pl.PESEL;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Getter
public class MusicianDTO extends UserDTO {

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthdate;
    @NotBlank(message = "Address is required")
    private String address;
    @PESEL(message = "Invalid Polish national identification number (PESEL)")
    private String pesel;
    @NotNull(message = "Tax office must be selected")
    private TaxOffice taxOffice;
    @NotBlank(message = "Bank account number is required")
    private String bankAccountNumber;
    private final Instrument instrument;
    private final Set<Project> pendingProjects;
    private final Set<Project> acceptedProjects;
    private final Set<Project> rejectedProjects;

    public MusicianDTO(Long id, String firstName, String lastName, String email, String password, Set<UserRole> roles, LocalDate birthdate, String address, String pesel, TaxOffice taxOffice, String bankAccountNumber, Instrument instrument, Set<Project> pendingProjects, Set<Project> acceptedProjects, Set<Project> rejectedProjects) {
        super(id, firstName, lastName, email, password, roles);
        this.birthdate = birthdate;
        this.address = address;
        this.pesel = pesel;
        this.taxOffice = taxOffice;
        this.bankAccountNumber = bankAccountNumber;
        this.instrument = instrument;
        this.pendingProjects = pendingProjects;
        this.acceptedProjects = acceptedProjects;
        this.rejectedProjects = rejectedProjects;
    }
}
