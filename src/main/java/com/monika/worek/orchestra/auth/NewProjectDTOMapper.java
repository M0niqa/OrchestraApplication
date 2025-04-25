package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.NewProjectDTO;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Project;

import java.util.HashSet;

public class NewProjectDTOMapper {

    public static NewProjectDTO mapToDTO(Project project) {
        if (project == null) {
            return null;
        }
        return NewProjectDTO.builder()
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .build();
    }

    public static Project mapToEntity(NewProjectDTO dto) {
        if (dto == null) {
            return null;
        }
        return Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }
}
