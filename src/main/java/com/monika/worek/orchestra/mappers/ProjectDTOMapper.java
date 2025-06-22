package com.monika.worek.orchestra.mappers;

import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Project;

import java.util.stream.Collectors;

public class ProjectDTOMapper {

    public static ProjectDTO mapToDto(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .location(project.getLocation())
                .conductor(project.getConductor())
                .programme(project.getProgramme())
                .projectMembers(project.getProjectMembers() != null
                        ? project.getProjectMembers().stream()
                        .map(MusicianBasicDTOMapper::mapToDto)
                        .collect(Collectors.toSet())
                        : null)
                .musiciansWhoRejected(project.getMusiciansWhoRejected() != null
                        ? project.getMusiciansWhoRejected().stream()
                        .map(MusicianBasicDTOMapper::mapToDto)
                        .collect(Collectors.toSet())
                        : null)
                .invited(project.getInvited() != null
                        ? project.getInvited().stream()
                        .map(MusicianBasicDTOMapper::mapToDto)
                        .collect(Collectors.toSet())
                        : null)
                .musicScores(project.getMusicScores())
                .agreementTemplate(project.getAgreementTemplate())
                .survey(project.getSurvey())
                .build();
    }
}
