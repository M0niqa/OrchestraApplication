package com.monika.worek.orchestra.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class MusicScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String filePath; // Store the file location

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public MusicScore(String fileName, String fileType, String filePath, Project project) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.project = project;
    }
}
