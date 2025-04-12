package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.UserRole;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
public class MusicianDTO extends UserDTO {
    private LocalDate birthdate;
    private String address;
    private String pesel;
    private String taxOffice;
    private String bankAccountNumber;
    private Instrument instrument;
    private Set<Project> pendingProjects;
    private Set<Project> acceptedProjects;
    private Set<Project> refusedProjects;

    public MusicianDTO(Long id, String firstName, String lastName, String email, String password, Set<UserRole> roles, LocalDate birthdate, String address, String pesel, String taxOffice, String bankAccountNumber, Instrument instrument, Set<Project> pendingProjects, Set<Project> acceptedProjects, Set<Project> refusedProjects) {
        super(id, firstName, lastName, email, password, roles);
        this.birthdate = birthdate;
        this.address = address;
        this.pesel = pesel;
        this.taxOffice = taxOffice;
        this.bankAccountNumber = bankAccountNumber;
        this.instrument = instrument;
        this.pendingProjects = pendingProjects;
        this.acceptedProjects = acceptedProjects;
        this.refusedProjects = refusedProjects;
    }
}
