package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.pl.NIP;
import org.hibernate.validator.constraints.pl.PESEL;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("Musician")
public class Musician extends User {
    private LocalDate birthdate;
    private String address;
    private String pesel;
    private String nip;
    private String companyName;
    @Enumerated(EnumType.STRING)
    private TaxOffice taxOffice;
    private String bankAccountNumber;
    @Enumerated(EnumType.STRING)
    private Instrument instrument;

    @ManyToMany(mappedBy = "invited")
    private Set<Project> pendingProjects = new HashSet<>();

    @ManyToMany(mappedBy = "projectMembers")
    private Set<Project> acceptedProjects = new HashSet<>();

    @ManyToMany(mappedBy = "musiciansWhoRejected")
    private Set<Project> rejectedProjects = new HashSet<>();


    @Override
    public String toString() {
        return "Musician{" +
                ", instrument=" + instrument +
                '}';
    }
}
