package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AgreementTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
//    @Enumerated(EnumType.STRING)
//    TemplateType templateType;
    @OneToMany(mappedBy = "agreementTemplate", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Project> projects;

    public AgreementTemplate(String content) {
        this.content = content;
    }
}