package com.monika.worek.orchestra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyQuestionDTO {

    @NotBlank(message = "Question text is required")
    private String questionText;

    private Long projectId;
}
