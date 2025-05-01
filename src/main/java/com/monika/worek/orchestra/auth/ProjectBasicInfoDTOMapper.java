package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.model.Project;

public class ProjectBasicInfoDTOMapper {

    public static ProjectBasicInfoDTO mapToDto(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectBasicInfoDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .location(project.getLocation())
                .description(project.getDescription())
                .build();
    }

    public static Project mapToEntity(ProjectBasicInfoDTO dto) {
        if (dto == null) {
            return null;
        }
        return Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .build();
    }
}
