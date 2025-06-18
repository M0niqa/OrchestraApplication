package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.constraint.StartDateBeforeEndDate;
import com.monika.worek.orchestra.model.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@StartDateBeforeEndDate
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;
    @NotBlank(message = "Location is required")
    private String location;
    private String conductor;
    private String programme;
    private Set<MusicianBasicDTO> projectMembers;
    private Set<MusicianBasicDTO> musiciansWhoRejected;
    private Set<MusicianBasicDTO> invited;
    private Set<MusicScore> musicScores;
    private AgreementTemplate agreementTemplate;
    private Survey survey;
    private Map<Instrument, Integer> instrumentCounts = new EnumMap<>(Instrument.class);
    private Map<String, BigDecimal> groupSalaries = new HashMap<>();
}
