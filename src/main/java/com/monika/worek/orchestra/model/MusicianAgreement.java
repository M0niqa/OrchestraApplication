package com.monika.worek.orchestra.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class MusicianAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String filePath;

    @ManyToOne
    private Musician musician;

    @ManyToOne
    private Project project;

    private LocalDateTime createdAt;
}