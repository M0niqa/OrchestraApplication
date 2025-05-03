package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private String location;
    private String conductor;
    private String programme;
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
    @ManyToOne(cascade = CascadeType.PERSIST)
    private AgreementTemplate agreementTemplate;
    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Survey survey;
    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "musicians_needed")
    private Map<Instrument, Integer> instrumentCounts = new EnumMap<>(Instrument.class);
    @ElementCollection
    @CollectionTable(name = "project_group_salaries", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyColumn(name = "instrument_group")
    @Column(name = "salary")
    private Map<String, BigDecimal> groupSalaries = new HashMap<>();
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime invitationDeadline;
}
