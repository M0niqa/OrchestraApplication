package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Survey;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewProjectDTO {

    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate endDate;
    private Survey survey = new Survey();

}