package com.monika.worek.orchestra.mappers;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.model.Project;

import java.util.List;

public class ProjectBasicInfoDTOMapper {

    public static ProjectBasicInfoDTO mapToDto(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectBasicInfoDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .location(project.getLocation())
                .conductor(project.getConductor())
                .programme(project.getProgramme())
                .build();
    }

    public static Project mapToEntity(ProjectBasicInfoDTO dto) {
        if (dto == null) {
            return null;
        }
        return Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .location(dto.getLocation())
                .conductor(dto.getConductor())
                .programme(dto.getProgramme())
                .build();
    }

    public static List<ProjectBasicInfoDTO> mapToListDTO(List<Project> projects) {
        return projects.stream()
                .map(ProjectBasicInfoDTOMapper::mapToDto)
                .toList();
    }
}
