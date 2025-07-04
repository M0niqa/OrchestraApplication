package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.mappers.MusicianBasicDTOMapper;
import com.monika.worek.orchestra.mappers.ProjectBasicInfoDTOMapper;
import com.monika.worek.orchestra.mappers.ProjectDTOMapper;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicianAgreementRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MusicianService musicianService;
    private final EmailService emailService;
    private final MusicianAgreementRepository musicianAgreementRepository;

    public ProjectService(ProjectRepository projectRepository, MusicianService musicianService, EmailService emailService, MusicianAgreementRepository musicianAgreementRepository) {
        this.projectRepository = projectRepository;
        this.musicianService = musicianService;
        this.emailService = emailService;
        this.musicianAgreementRepository = musicianAgreementRepository;
    }

    public void inviteMusician(Long projectId, Long musicianId, LocalDateTime invitationDeadline) {
        Project project = getProjectById(projectId);
        Musician musician = musicianService.getMusicianById(musicianId);

        if (!project.getInvited().contains(musician) &&
                !project.getProjectMembers().contains(musician)
                && !project.getMusiciansWhoRejected().contains(musician)) {

            project.getInvited().add(musician);
            project.setInvitationDeadline(invitationDeadline);
            projectRepository.save(project);

            String subject = "New Project Invitation";
            String link = "http://localhost:8080/login";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDeadline = invitationDeadline.format(formatter);
            String text = "You have been invited to join the project: " + project.getName() + ".\n" +
                    "Log in to the system (" + link + ") to check details, " +
                    "and accept or reject the project by " + formattedDeadline + ".";

            emailService.sendEmail(musician.getEmail(), subject, text);
        }
    }

    @Transactional
    public void acceptInvitation(Long projectId, Long musicianId) {
        Project project = getProjectById(projectId);
        Musician musician = musicianService.getMusicianById(musicianId);

        project.getInvited().remove(musician);
        project.getProjectMembers().add(musician);

        projectRepository.save(project);
    }

    @Transactional
    public void rejectInvitation(Long projectId, Long musicianId) {
        Project project = getProjectById(projectId);
        Musician musician = musicianService.getMusicianById(musicianId);
        musicianAgreementRepository.deleteByMusicianIdAndProjectId(musicianId, projectId);

        project.getInvited().remove(musician);
        project.getMusiciansWhoRejected().add(musician);

        projectRepository.save(project);
    }

    public void throwIfUnauthorized(Long projectId, String email) {
        Project project = getProjectById(projectId);
        Musician musician = musicianService.getMusicianByEmail(email);

        if (!project.getInvited().contains(musician) && !project.getProjectMembers().contains(musician)) {
            throw new AccessDeniedException("You are not a member of this project.");
        }
    }

    @Transactional
    public void updatePendingInvitations(Long projectId) {
        Project project = getProjectById(projectId);
        LocalDateTime invitationDeadline = project.getInvitationDeadline();
        if (invitationDeadline == null) {
            return;
        }
        if (LocalDateTime.now().isAfter(invitationDeadline)) {
            project.getMusiciansWhoRejected().addAll(project.getInvited());
            project.getInvited().clear();
            projectRepository.save(project);
        }
    }

    public LinkedHashMap<Instrument, List<MusicianBasicDTO>> getAvailableMusiciansByInstrument(Long projectId) {
        return getAvailableMusiciansSorted(projectId).stream()
                .sorted(Comparator.comparingInt(musician -> musician.getInstrument().ordinal()))
                .map(MusicianBasicDTOMapper::mapToDto)
                .collect(Collectors.groupingBy(
                        MusicianBasicDTO::instrument,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private List<Musician> getAvailableMusiciansSorted(Long projectId) {
        Project project = getProjectById(projectId);

        List<Musician> allMusicians = musicianService.getAllMusiciansSortedBySurname();

        return allMusicians.stream()
                .filter(musician -> !project.getProjectMembers().contains(musician) &&
                        !project.getMusiciansWhoRejected().contains(musician) &&
                        !project.getInvited().contains(musician))
                .sorted(Comparator.comparingInt((Musician m) -> m.getAcceptedProjects().size()).reversed())
                .toList();
    }

    public LinkedHashMap<Instrument, List<MusicianBasicDTO>> getProjectMembersByInstrument(Project project) {
        List<Musician> sortedMembers = project.getProjectMembers().stream()
                .sorted(Comparator.comparingInt((Musician m) -> m.getAcceptedProjects().size()).reversed())
                .toList();

        return sortedMembers.stream()
                .sorted(Comparator.comparingInt(musician -> musician.getInstrument().ordinal()))
                .map(MusicianBasicDTOMapper::mapToDto)
                .collect(Collectors.groupingBy(
                        MusicianBasicDTO::instrument,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public Map<Instrument, Integer> getRemainingInstrumentsCount(Project project) {
        Map<Instrument, Integer> remainingCounts = new EnumMap<>(Instrument.class);
        for (Instrument instrument : Instrument.values()) {
            int required = project.getInstrumentCounts().getOrDefault(instrument, 0);
            long accepted = getProjectMembersCountByInstrument(project).getOrDefault(instrument, 0L);
            int remaining = Math.max(required - (int) accepted, 0);
            remainingCounts.put(instrument, remaining);
        }
        return remainingCounts;
    }

    private Map<Instrument, Long> getProjectMembersCountByInstrument(Project project) {
        return project.getProjectMembers().stream()
                .collect(Collectors.groupingBy(Musician::getInstrument, Collectors.counting()));
    }

    @Transactional
    public void removeProjectMember(Long projectId, Long musicianId) {
        Project project = getProjectById(projectId);
        project.getProjectMembers().removeIf(musician -> musician.getId().equals(musicianId));
        project.getMusiciansWhoRejected().add(musicianService.getMusicianById(musicianId));
        musicianAgreementRepository.deleteByMusicianIdAndProjectId(musicianId, projectId);
        projectRepository.save(project);
    }

    public List<ProjectBasicInfoDTO> getOngoingProjectsDTOs() {
        LocalDate today = LocalDate.now();
        List<Project> projects = projectRepository.findByStartDateBeforeAndEndDateAfter(today.plusDays(1), today.minusDays(1));
        return projects.stream().sorted().map(ProjectBasicInfoDTOMapper::mapToDto).toList();
    }

    public List<ProjectBasicInfoDTO> getFutureProjectsDTOs() {
        LocalDate today = LocalDate.now();
        List<Project> projects = projectRepository.findByStartDateAfter(today);
        return projects.stream().sorted().map(ProjectBasicInfoDTOMapper::mapToDto).toList();
    }

    public List<ProjectBasicInfoDTO> getFutureProjectsDTOsByMusicianId(Long musicianId) {
        LocalDate today = LocalDate.now().plusDays(7);
        List<Project> projects = projectRepository.findByStartDateAfter(today);
        Musician musician = musicianService.getMusicianById(musicianId);
        return projects.stream().filter(project -> !project.getProjectMembers().contains(musician))
                .filter(project -> !project.getInvited().contains(musician))
                .filter(project -> !project.getMusiciansWhoRejected().contains(musician))
                .sorted().map(ProjectBasicInfoDTOMapper::mapToDto).toList();

    }

    public List<ProjectBasicInfoDTO> getArchivedProjectsDTOs() {
        LocalDate today = LocalDate.now();
        List<Project> projects = projectRepository.findByEndDateBefore(today);
        return projects.stream().sorted().map(ProjectBasicInfoDTOMapper::mapToDto).toList();
    }

    @Transactional
    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public ProjectDTO getProjectDtoById(Long id) {
        return ProjectDTOMapper.mapToDto(getProjectById(id));
    }

    public ProjectBasicInfoDTO getProjectBasicDtoById(Long id) {
        return ProjectBasicInfoDTOMapper.mapToDto(getProjectById(id));
    }

    @Transactional
    public void updateBasicProjectInfo(Long id, ProjectBasicInfoDTO dto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setLocation(dto.getLocation());
        project.setConductor(dto.getConductor());
        project.setProgramme(dto.getProgramme());
    }

    public void saveProject(Project project) {
        projectRepository.save(project);
    }
}
