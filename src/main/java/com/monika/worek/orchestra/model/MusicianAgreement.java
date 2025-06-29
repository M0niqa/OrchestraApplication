package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MusicianAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Musician musician;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}