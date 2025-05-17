package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgreementTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @OneToMany(mappedBy = "agreementTemplate", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Project> projects;
}