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
    @JoinTable(
            name = "accepted_musicians_projects",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "musician_id")
    )
    private Set<Musician> projectMembers;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rejected_musicians_projects",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "musician_id")
    )
    private Set<Musician> musiciansWhoRejected;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "invited_musicians_projects",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "musician_id")
    )
    private Set<Musician> invited;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MusicScore> musicScores = new HashSet<>();
    @ManyToOne
    private AgreementTemplate agreementTemplate;
    @OneToOne(mappedBy = "project")
    private Survey survey;
}
