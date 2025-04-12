package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Musician> projectMembers;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Musician> musiciansWhoRefused;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Musician> invited;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MusicScore> files = new HashSet<>();
    @ManyToOne
    private AgreementTemplate agreementTemplate;
}
