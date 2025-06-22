package com.monika.worek.orchestra.mapper;

import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.mappers.ProjectDTOMapper;
import com.monika.worek.orchestra.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectDTOMapperTest {

    @Test
    void mapToDto_whenGivenNullProject_thenShouldReturnNull() {
        // given
        Project project = null;

        // when
        ProjectDTO dto = ProjectDTOMapper.mapToDto(project);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void mapToDto_whenProjectHasNullCollections_thenShouldMapToDtoWithNullSets() {
        // given
        Project project = Project.builder()
                .id(1L)
                .name("Project with Null Sets")
                .projectMembers(null)
                .musiciansWhoRejected(null)
                .invited(null)
                .build();

        // when
        ProjectDTO dto = ProjectDTOMapper.mapToDto(project);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getProjectMembers()).isNull();
        assertThat(dto.getMusiciansWhoRejected()).isNull();
        assertThat(dto.getInvited()).isNull();
    }

    @Test
    void mapToDto_whenProjectHasEmptyCollections_thenShouldMapToDtoWithEmptySets() {
        // given
        Project project = Project.builder()
                .id(1L)
                .name("Project with Empty Sets")
                .projectMembers(new HashSet<>())
                .musiciansWhoRejected(new HashSet<>())
                .invited(new HashSet<>())
                .build();

        // when
        ProjectDTO dto = ProjectDTOMapper.mapToDto(project);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getProjectMembers()).isNotNull().isEmpty();
        assertThat(dto.getMusiciansWhoRejected()).isNotNull().isEmpty();
        assertThat(dto.getInvited()).isNotNull().isEmpty();
    }

    @Test
    void mapToDto_whenGivenValidProject_thenShouldMapAllFieldsCorrectly() {
        // given
        Musician member = Musician.builder().id(1L).firstName("John").lastName("Doe").instrument(Instrument.CELLO).build();
        Musician rejected = Musician.builder().id(2L).firstName("Jane").lastName("Smith").instrument(Instrument.VIOLA).build();
        Musician invited = Musician.builder().id(3L).firstName("Peter").lastName("Jones").instrument(Instrument.FLUTE).build();

        Project project = Project.builder()
                .id(1L)
                .name("Grand Gala")
                .description("A fantastic concert.")
                .startDate(LocalDate.of(2025, 10, 1))
                .endDate(LocalDate.of(2025, 10, 5))
                .location("National Hall")
                .conductor("Leonard Bernstein")
                .programme("Symphony No. 5")
                .projectMembers(Set.of(member))
                .musiciansWhoRejected(Set.of(rejected))
                .invited(Set.of(invited))
                .musicScores(Set.of(new MusicScore()))
                .agreementTemplate(new AgreementTemplate())
                .survey(new Survey())
                .build();

        // when
        ProjectDTO dto = ProjectDTOMapper.mapToDto(project);

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

        assertThat(dto.getProjectMembers()).hasSize(1);
        assertThat(dto.getProjectMembers().iterator().next().firstName()).isEqualTo("John");

        assertThat(dto.getMusiciansWhoRejected()).hasSize(1);
        assertThat(dto.getMusiciansWhoRejected().iterator().next().firstName()).isEqualTo("Jane");

        assertThat(dto.getInvited()).hasSize(1);
        assertThat(dto.getInvited().iterator().next().firstName()).isEqualTo("Peter");

        assertThat(dto.getMusicScores()).isEqualTo(project.getMusicScores());
        assertThat(dto.getAgreementTemplate()).isEqualTo(project.getAgreementTemplate());
        assertThat(dto.getSurvey()).isEqualTo(project.getSurvey());
    }
}
