package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    private Set<MusicianBasicDTO> projectMembers;
    private Set<MusicianBasicDTO> musiciansWhoRejected;
    private Set<MusicianBasicDTO> invited;
    private Set<MusicScore> musicScores;
    private AgreementTemplate agreementTemplate;
    private Survey survey;
    private Map<Instrument, Integer> instrumentCounts = new EnumMap<>(Instrument.class);
    private Map<String, BigDecimal> groupSalaries = new HashMap<>();
}
