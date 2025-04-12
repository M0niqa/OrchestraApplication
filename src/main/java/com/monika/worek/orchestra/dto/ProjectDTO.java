package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Status;
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

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<Musician> projectMembers;
    private Set<Musician> musiciansWhoRefused;
    private Set<Musician> invited;
    private Status status;

}
