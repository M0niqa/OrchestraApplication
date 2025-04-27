package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    private int yesCount;
    private int noCount;

    @ManyToOne
    private Survey survey;
}

