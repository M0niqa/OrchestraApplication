package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.auth.MusicianBasicDTOMapper;
import com.monika.worek.orchestra.auth.ProjectBasicInfoDTOMapper;
import com.monika.worek.orchestra.auth.ProjectDTOMapper;
import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MusicianRepository musicianRepository;
    private final EmailService emailService;

    public ProjectService(ProjectRepository projectRepository, MusicianRepository musicianRepository, EmailService emailService) {
        this.projectRepository = projectRepository;
        this.musicianRepository = musicianRepository;
        this.emailService = emailService;
    }

    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    public void inviteMusician(Long projectId, Long musicianId, LocalDateTime invitationDeadline) {
        Project project = findProjectById(projectId);
        Musician musician = findMusicianById(musicianId);

        if (!project.getInvited().contains(musician) &&
                !project.getProjectMembers().contains(musician)
                && !project.getMusiciansWhoRejected().contains(musician)) {

            project.getInvited().add(musician);
            musician.getPendingProjects().add(project);
            project.setInvitationDeadline(invitationDeadline);
            projectRepository.save(project);
        }

        String subject = "New Project Invitation";
        String link = "http://localhost:8080/login";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDeadline = invitationDeadline.format(formatter);
        String text = "You have been invited to join the project: " + project.getName() + ".\n" +
                "Log in to the system (" + link + ") to check details, " +
                "and accept or reject the project by " + formattedDeadline + ".";

        emailService.sendEmail(musician.getEmail(), subject, text);
    }

    @Transactional
    public void acceptInvitation(Long projectId, Long musicianId) {
        Project project = findProjectById(projectId);
        Musician musician = findMusicianById(musicianId);

        if (!project.getInvited().contains(musician)) {
            throw new IllegalStateException("Musician not currently invited to this project.");
        }

        project.getInvited().remove(musician);
        project.getProjectMembers().add(musician);

        projectRepository.save(project);
    }

    @Transactional
    public void rejectInvitation(Long projectId, Long musicianId) {
        Project project = findProjectById(projectId);
        Musician musician = findMusicianById(musicianId);

        if (!project.getInvited().contains(musician)) {
            throw new IllegalStateException("Musician not currently invited to this project.");
        }

        project.getInvited().remove(musician);
        project.getMusiciansWhoRejected().add(musician);

        projectRepository.save(project);
    }

    @Transactional
    public void updatePendingInvitations(Long projectId) {
        Project project = findProjectById(projectId);
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

    private List<Musician> getAvailableMusicians(Long projectId) {
        Project project = findProjectById(projectId);

        List<Musician> allMusicians = (List<Musician>) musicianRepository.findAll();

        return allMusicians.stream()
                .filter(musician -> !project.getProjectMembers().contains(musician) &&
                        !project.getMusiciansWhoRejected().contains(musician) &&
                        !project.getInvited().contains(musician))
                .collect(Collectors.toList());
    }

    public LinkedHashMap<Instrument, List<MusicianBasicDTO>> getAvailableMusiciansByInstrument(Long projectId) {
        return getAvailableMusicians(projectId).stream()
                .sorted(Comparator.comparingInt(musician -> musician.getInstrument().ordinal()))
                .map(MusicianBasicDTOMapper::mapToDto)
                .collect(Collectors.groupingBy(
                        MusicianBasicDTO::getInstrument,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public LinkedHashMap<Instrument, List<MusicianBasicDTO>> getProjectMembersByInstrument(Project project) {
        return project.getProjectMembers().stream()
                .sorted(Comparator.comparingInt(musician -> musician.getInstrument().ordinal()))
                .map(MusicianBasicDTOMapper::mapToDto)
                .collect(Collectors.groupingBy(
                        MusicianBasicDTO::getInstrument,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public Map<Instrument, Long> getProjectMembersCountByInstrument(Project project) {
        return project.getProjectMembers().stream()
                .collect(Collectors.groupingBy(Musician::getInstrument, Collectors.counting()));
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

    public List<ProjectBasicInfoDTO> getOngoingProjects() {
        LocalDate today = LocalDate.now();
        List<Project> projects = projectRepository.findByStartDateBeforeAndEndDateAfter(today.plusDays(1), today.minusDays(1));
        return projects.stream().map(ProjectBasicInfoDTOMapper::mapToDto).collect(Collectors.toList());
    }

    public List<ProjectBasicInfoDTO> getFutureProjects() {
        LocalDate today = LocalDate.now();
        List<Project> projects = projectRepository.findByStartDateAfter(today);
        return projects.stream().map(ProjectBasicInfoDTOMapper::mapToDto).collect(Collectors.toList());
    }

    public List<ProjectBasicInfoDTO> getArchivedProjects() {
        LocalDate today = LocalDate.now();
        List<Project> projects = projectRepository.findByEndDateBefore(today);
        return projects.stream().map(ProjectBasicInfoDTOMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    public ProjectDTO getProjectDtoById(Long id) {
        return ProjectDTOMapper.mapToDto(getProjectById(id));
    }

    public ProjectBasicInfoDTO getProjectBasicDtoById(Long id) {
        return ProjectBasicInfoDTOMapper.mapToDto(getProjectById(id));
    }

    @Transactional
    public void updateBasicProjectInfo(Long id, ProjectBasicInfoDTO dto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setLocation(dto.getLocation());
        project.setConductor(dto.getConductor());
        project.setProgramme(dto.getProgramme());
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    private Musician findMusicianById(Long id) {
        return musicianRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Musician not found"));
    }
}