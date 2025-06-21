package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicianAgreementRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MusicianService musicianService;
    @Mock
    private EmailService emailService;
    @Mock
    private MusicianAgreementRepository musicianAgreementRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private Musician musician;

    @BeforeEach
    void setUp() {
        musician = Musician.builder()
                .id(1L)
                .email("musician@example.com")
                .roles(new HashSet<>())
                .acceptedProjects(new HashSet<>())
                .pendingProjects(new HashSet<>())
                .rejectedProjects(new HashSet<>())
                .build();

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .invited(new HashSet<>())
                .projectMembers(new HashSet<>())
                .musiciansWhoRejected(new HashSet<>())
                .build();
    }

    @Test
    void inviteMusician_whenMusicianIsNotAlreadyInvolved_thenShouldAddToInvitedListAndSendEmail() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(musicianService.getMusicianById(1L)).thenReturn(musician);

        // when
        projectService.inviteMusician(1L, 1L, LocalDateTime.now().plusDays(7));

        // then
        assertThat(project.getInvited()).contains(musician);
        verify(projectRepository, times(1)).save(project);
        verify(emailService, times(1)).sendEmail(eq(musician.getEmail()), anyString(), anyString());
    }

    @Test
    void inviteMusician_whenMusicianIsAlreadyAMember_thenShouldDoNothing() {
        // given
        project.getProjectMembers().add(musician);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(musicianService.getMusicianById(1L)).thenReturn(musician);

        // when
        projectService.inviteMusician(1L, 1L, LocalDateTime.now().plusDays(7));

        // then
        verify(projectRepository, never()).save(any());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    void acceptInvitation_whenCalled_thenShouldMoveMusicianFromInvitedToMembers() {
        // given
        project.getInvited().add(musician);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(musicianService.getMusicianById(1L)).thenReturn(musician);

        // when
        projectService.acceptInvitation(1L, 1L);

        // then
        assertThat(project.getInvited()).doesNotContain(musician);
        assertThat(project.getProjectMembers()).contains(musician);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void rejectInvitation_whenCalled_thenShouldMoveMusicianFromInvitedToRejected() {
        // given
        project.getInvited().add(musician);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(musicianService.getMusicianById(1L)).thenReturn(musician);

        // when
        projectService.rejectInvitation(1L, 1L);

        // then
        assertThat(project.getInvited()).doesNotContain(musician);
        assertThat(project.getMusiciansWhoRejected()).contains(musician);
        verify(projectRepository, times(1)).save(project);
        verify(musicianAgreementRepository, times(1)).deleteByMusicianIdAndProjectId(1L, 1L);
    }

    @Test
    void throwIfUnauthorized_whenMusicianIsMember_thenShouldNotThrowException() {
        // given
        project.getProjectMembers().add(musician);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(musicianService.getMusicianByEmail("musician@example.com")).thenReturn(musician);

        // when
        // then
        assertDoesNotThrow(() -> projectService.throwIfUnauthorized(1L, "musician@example.com"));
    }

    @Test
    void throwIfUnauthorized_whenMusicianIsNotInvolved_thenShouldThrowAccessDeniedException() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(musicianService.getMusicianByEmail("musician@example.com")).thenReturn(musician);

        // when
        // then
        assertThatThrownBy(() -> projectService.throwIfUnauthorized(1L, "musician@example.com"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You are not a member of this project.");
    }

    @Test
    void updatePendingInvitations_whenDeadlineHasPassed_thenShouldMoveInvitedToRejected() {
        // given
        project.getInvited().add(musician);
        project.setInvitationDeadline(LocalDateTime.now().minusHours(1));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // when
        projectService.updatePendingInvitations(1L);

        // then
        assertThat(project.getInvited()).isEmpty();
        assertThat(project.getMusiciansWhoRejected()).contains(musician);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void updatePendingInvitations_whenDeadlineHasNotPassed_thenShouldDoNothing() {
        // given
        project.getInvited().add(musician);
        project.setInvitationDeadline(LocalDateTime.now().plusHours(1));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // when
        projectService.updatePendingInvitations(1L);

        // then
        assertThat(project.getInvited()).contains(musician);
        assertThat(project.getMusiciansWhoRejected()).isEmpty();
        verify(projectRepository, never()).save(project);
    }

    @Test
    void removeProjectMember_whenCalled_thenShouldRemoveFromMembersAndAddToRejected() {
        // given
        project.getProjectMembers().add(musician);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(musicianService.getMusicianById(1L)).thenReturn(musician);

        // when
        projectService.removeProjectMember(1L, 1L);

        // then
        assertThat(project.getProjectMembers()).doesNotContain(musician);
        assertThat(project.getMusiciansWhoRejected()).contains(musician);
        verify(projectRepository, times(1)).save(project);
        verify(musicianAgreementRepository, times(1)).deleteByMusicianIdAndProjectId(1L, 1L);
    }

    @Test
    void getAvailableMusiciansByInstrument_whenCalled_thenShouldReturnFilteredAndSortedMusicians() {
        // given
        Musician availableViolin = Musician.builder().id(2L).firstName("Anna").lastName("Nowak").instrument(Instrument.VIOLIN_I).acceptedProjects(new HashSet<>()).build();
        Musician availableCello = Musician.builder().id(3L).firstName("Piotr").lastName("Kowalski").instrument(Instrument.CELLO).acceptedProjects(new HashSet<>()).build();
        project.getProjectMembers().add(musician);

        List<Musician> allMusicians = List.of(musician, availableViolin, availableCello);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(musicianService.getAllMusiciansSortedBySurname()).thenReturn(allMusicians);

        // when
        LinkedHashMap<Instrument, List<MusicianBasicDTO>> result = projectService.getAvailableMusiciansByInstrument(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2); // only cello and violin I should be present
        assertThat(result.containsKey(Instrument.VIOLIN_I)).isTrue();
        assertThat(result.containsKey(Instrument.CELLO)).isTrue();
        assertThat(result.get(Instrument.VIOLIN_I).getFirst().firstName()).isEqualTo("Anna");
        assertThat(result.get(Instrument.CELLO).getFirst().firstName()).isEqualTo("Piotr");
    }

    @Test
    void getProjectMembersByInstrument_whenCalled_thenShouldReturnGroupedAndSortedMembers() {
        // given
        Musician memberCello = Musician.builder().id(2L).firstName("Piotr").lastName("Kowalski").instrument(Instrument.CELLO).acceptedProjects(Set.of(project)).build();
        Musician memberViolin = Musician.builder().id(3L).firstName("Anna").lastName("Nowak").instrument(Instrument.VIOLIN_I).acceptedProjects(Set.of()).build();
        project.setProjectMembers(new HashSet<>(List.of(memberCello, memberViolin)));

        // when
        LinkedHashMap<Instrument, List<MusicianBasicDTO>> result = projectService.getProjectMembersByInstrument(project);

        // then
        assertThat(result).isNotNull();
        assertThat(result.keySet().stream().map(Instrument::name).collect(Collectors.toList()))
                .isSortedAccordingTo(Comparator.comparingInt(i -> Instrument.valueOf(i).ordinal()));

        assertThat(result.get(Instrument.CELLO).getFirst().firstName()).isEqualTo("Piotr");
    }

    @Test
    void getRemainingInstrumentsCount_whenMembersExist_thenReturnsCorrectRemainingCounts() {
        // given
        Map<Instrument, Integer> requiredCounts = new EnumMap<>(Instrument.class);
        requiredCounts.put(Instrument.VIOLIN_I, 2);
        requiredCounts.put(Instrument.CELLO, 1);
        project.setInstrumentCounts(requiredCounts);

        Musician memberViolin = Musician.builder().instrument(Instrument.VIOLIN_I).build();
        project.getProjectMembers().add(memberViolin);

        // when
        Map<Instrument, Integer> remaining = projectService.getRemainingInstrumentsCount(project);

        // then
        assertThat(remaining).isNotNull();
        assertThat(remaining.get(Instrument.VIOLIN_I)).isEqualTo(1); // 2 required - 1 accepted = 1 remaining
        assertThat(remaining.get(Instrument.CELLO)).isEqualTo(1); // 1 required - 0 accepted = 1 remaining
        assertThat(remaining.get(Instrument.FLUTE)).isEqualTo(0); // 0 required - 0 accepted = 0 remaining
    }

    @Test
    void getFutureProjectsDTOs_whenCalled_thenReturnsProjectsFromFuture() {
        // given
        Project futureProject = Project.builder().id(10L).name("Future Project").startDate(LocalDate.now().plusDays(10)).build();
        List<Project> projectsFromRepo = List.of(futureProject);
        when(projectRepository.findByStartDateAfter(any(LocalDate.class))).thenReturn(projectsFromRepo);

        // when
        List<ProjectBasicInfoDTO> result = projectService.getFutureProjectsDTOs();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Future Project");
        verify(projectRepository, times(1)).findByStartDateAfter(any(LocalDate.class));
    }

    @Test
    void getFutureProjectsDTOs_whenProjectsExist_thenShouldReturnMappedDTOs() {
        // given
        Project futureProject = Project.builder()
                .id(1L)
                .name("Future Concert")
                .startDate(LocalDate.now().plusDays(10))
                .build();
        List<Project> projectsFromRepo = List.of(futureProject);
        when(projectRepository.findByStartDateAfter(any(LocalDate.class))).thenReturn(projectsFromRepo);

        // when
        List<ProjectBasicInfoDTO> resultDTOs = projectService.getFutureProjectsDTOs();

        // then
        assertThat(resultDTOs).isNotNull();
        assertThat(resultDTOs).hasSize(1);
        assertThat(resultDTOs.getFirst().getName()).isEqualTo("Future Concert");
    }

    @Test
    void getArchivedProjectsDTOs_whenNoProjectsExist_thenShouldReturnEmptyList() {
        // given
        when(projectRepository.findByEndDateBefore(any(LocalDate.class))).thenReturn(Collections.emptyList());

        // when
        List<ProjectBasicInfoDTO> resultDTOs = projectService.getArchivedProjectsDTOs();

        // then
        assertThat(resultDTOs).isNotNull();
        assertThat(resultDTOs).isEmpty();
    }

    @Test
    void deleteProjectById_whenCalled_thenShouldInvokeRepositoryDelete() {
        // given
        Long projectId = 1L;

        // when
        projectService.deleteProjectById(projectId);

        // then
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    void getProjectById_whenProjectExists_thenShouldReturnProject() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // when
        Project result = projectService.getProjectById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
    }

    @Test
    void updateBasicProjectInfo_whenCalled_thenShouldUpdateProjectFields() {
        // given
        Project existingProject = Project.builder()
                .id(1L)
                .name("Old Name")
                .location("Old Location")
                .build();

        ProjectBasicInfoDTO dto = ProjectBasicInfoDTO.builder()
                .name("New Name")
                .description("New Description")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 5))
                .location("New Location")
                .conductor("New Conductor")
                .programme("New Programme")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        // when
        projectService.updateBasicProjectInfo(1L, dto);

        // then
        assertThat(existingProject.getName()).isEqualTo("New Name");
        assertThat(existingProject.getDescription()).isEqualTo("New Description");
        assertThat(existingProject.getLocation()).isEqualTo("New Location");
        assertThat(existingProject.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @Test
    void saveProject_whenCalled_thenShouldInvokeRepositorySave() {
        // given
        Project projectToSave = Project.builder().id(1L).build();

        // when
        projectService.saveProject(projectToSave);

        // then
        verify(projectRepository, times(1)).save(projectToSave);
    }
}