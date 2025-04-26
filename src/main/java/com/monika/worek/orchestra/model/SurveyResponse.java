package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SurveyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private SurveyAnswer answer;

    @ManyToOne
    private Survey survey;

    @ManyToOne
    private SurveyQuestion question;
}