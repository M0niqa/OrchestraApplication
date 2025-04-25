package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Project;

import java.util.HashSet;

public class ProjectDTOMapper {

    public static ProjectDTO mapToDTO(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectMembers(project.getProjectMembers() != null ? new HashSet<>(project.getProjectMembers()) : null)
                .musiciansWhoRefused(project.getMusiciansWhoRefused() != null ? new HashSet<>(project.getMusiciansWhoRefused()) : null)
                .invited(project.getInvited() != null ? new HashSet<>(project.getInvited()) : null)
                .status(project.getStatus())
                .musicScores(project.getMusicScores())
                .agreementTemplate(project.getAgreementTemplate())
                .build();
    }

    public static Project mapToEntity(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }
        return Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .projectMembers(dto.getProjectMembers() != null ? new HashSet<>(dto.getProjectMembers()) : null)
                .musiciansWhoRefused(dto.getMusiciansWhoRefused() != null ? new HashSet<>(dto.getMusiciansWhoRefused()) : null)
                .invited(dto.getInvited() != null ? new HashSet<>(dto.getInvited()) : null)
                .status(dto.getStatus())
                .musicScores(dto.getMusicScores())
                .agreementTemplate(dto.getAgreementTemplate())
                .build();
    }
}
