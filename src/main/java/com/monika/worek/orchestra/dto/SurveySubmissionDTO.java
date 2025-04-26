package com.monika.worek.orchestra.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
public class SurveySubmissionDTO {

    private Map<Long, String> responses;

    public SurveySubmissionDTO() {
        this.responses = new HashMap<>();
    }
}
