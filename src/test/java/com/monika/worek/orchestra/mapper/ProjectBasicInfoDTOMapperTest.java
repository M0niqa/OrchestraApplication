package com.monika.worek.orchestra.mapper;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.dtoMappers.ProjectBasicInfoDTOMapper;
import com.monika.worek.orchestra.model.Project;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectBasicInfoDTOMapperTest {

    @Test
    void mapToDto_whenGivenNullProject_thenShouldReturnNull() {
        // given
        Project project = null;

        // when
        ProjectBasicInfoDTO dto = ProjectBasicInfoDTOMapper.mapToDto(project);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void mapToDto_whenGivenValidProject_thenShouldMapAllFields() {
        // given
        Project project = Project.builder()
                .id(1L)
                .name("Summer Symphony")
                .description("A summer concert series.")
                .startDate(LocalDate.of(2025, 7, 10))
                .endDate(LocalDate.of(2025, 7, 20))
                .location("Warsaw Philharmonic")
                .conductor("John Williams")
                .programme("Star Wars Suite")
                .build();

        // when
        ProjectBasicInfoDTO dto = ProjectBasicInfoDTOMapper.mapToDto(project);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(project.getId());
        assertThat(dto.getName()).isEqualTo(project.getName());
        assertThat(dto.getDescription()).isEqualTo(project.getDescription());
        assertThat(dto.getStartDate()).isEqualTo(project.getStartDate());
        assertThat(dto.getEndDate()).isEqualTo(project.getEndDate());
        assertThat(dto.getLocation()).isEqualTo(project.getLocation());
        assertThat(dto.getConductor()).isEqualTo(project.getConductor());
        assertThat(dto.getProgramme()).isEqualTo(project.getProgramme());
    }

    @Test
    void mapToEntity_whenGivenNullDto_thenShouldReturnNull() {
        // given
        ProjectBasicInfoDTO dto = null;

        // when
        Project entity = ProjectBasicInfoDTOMapper.mapToEntity(dto);

        // then
        assertThat(entity).isNull();
    }

    @Test
    void mapToEntity_whenGivenValidDto_thenShouldMapAllFields() {
        // given
        ProjectBasicInfoDTO dto = ProjectBasicInfoDTO.builder()
                .id(1L)
                .name("Summer Symphony")
                .description("A summer concert series.")
                .startDate(LocalDate.of(2025, 7, 10))
                .endDate(LocalDate.of(2025, 7, 20))
                .location("Warsaw Philharmonic")
                .conductor("John Williams")
                .programme("Star Wars Suite")
                .build();

        // when
        Project entity = ProjectBasicInfoDTOMapper.mapToEntity(dto);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
        assertThat(entity.getStartDate()).isEqualTo(dto.getStartDate());
        assertThat(entity.getEndDate()).isEqualTo(dto.getEndDate());
        assertThat(entity.getLocation()).isEqualTo(dto.getLocation());
        assertThat(entity.getConductor()).isEqualTo(dto.getConductor());
        assertThat(entity.getProgramme()).isEqualTo(dto.getProgramme());
    }

    @Test
    void mapToListDTO_whenGivenEmptyList_thenShouldReturnEmptyList() {
        // given
        List<Project> emptyProjectList = Collections.emptyList();

        // when
        List<ProjectBasicInfoDTO> dtoList = ProjectBasicInfoDTOMapper.mapToListDTO(emptyProjectList);

        // then
        assertThat(dtoList).isNotNull();
        assertThat(dtoList).isEmpty();
    }

    @Test
    void mapToListDTO_whenGivenProjectList_thenShouldReturnDtoList() {
        // given
        Project project1 = Project.builder().id(1L).name("Project 1").build();
        Project project2 = Project.builder().id(2L).name("Project 2").build();
        List<Project> projectList = List.of(project1, project2);

        // when
        List<ProjectBasicInfoDTO> dtoList = ProjectBasicInfoDTOMapper.mapToListDTO(projectList);

        // then
        assertThat(dtoList).isNotNull();
        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).getId()).isEqualTo(1L);
        assertThat(dtoList.get(0).getName()).isEqualTo("Project 1");
        assertThat(dtoList.get(1).getId()).isEqualTo(2L);
        assertThat(dtoList.get(1).getName()).isEqualTo("Project 2");
    }
}
