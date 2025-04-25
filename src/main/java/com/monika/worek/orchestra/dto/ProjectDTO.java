package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.MusicScore;
import com.monika.worek.orchestra.model.Musician;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private Set<Musician> projectMembers;
    private Set<Musician> musiciansWhoRefused;
    private Set<Musician> invited;
    private Set<MusicScore> musicScores;
    private AgreementTemplate agreementTemplate;
}
