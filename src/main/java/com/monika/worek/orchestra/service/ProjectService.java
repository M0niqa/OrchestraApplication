package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public void inviteMusician(Long projectId, Long musicianId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Musician musician = musicianRepository.findById(musicianId).orElseThrow(() -> new IllegalArgumentException("Musician not found"));

        if (!project.getInvited().contains(musician) &&
                !project.getProjectMembers().contains(musician)
                && !project.getMusiciansWhoRejected().contains(musician)) {

            project.getInvited().add(musician);
            musician.getPendingProjects().add(project);
            projectRepository.save(project);
        }

        String subject = "New Project Invitation";
        String text = "You have been invited to join the project: " + project.getName();
        emailService.sendEmail(musician.getEmail(), subject, text);
    }

    @Transactional
    public void acceptInvitation(Long projectId, Long musicianId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        Musician musician = musicianRepository.findById(musicianId).orElseThrow(() -> new IllegalArgumentException("Musician not found: " + musicianId));

        if (!project.getInvited().contains(musician)) {
            throw new IllegalStateException("Musician not currently invited to this project.");
        }

        project.getInvited().remove(musician);
        project.getProjectMembers().add(musician);
        musician.getPendingProjects().remove(project);

        projectRepository.save(project);
        // musicianRepository.save(musician); // If needed
    }

    @Transactional
    public void rejectInvitation(Long projectId, Long musicianId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        Musician musician = musicianRepository.findById(musicianId).orElseThrow(() -> new IllegalArgumentException("Musician not found: " + musicianId));

        if (!project.getInvited().contains(musician)) {
            throw new IllegalStateException("Musician not currently invited to this project.");
        }

        project.getInvited().remove(musician);
        project.getMusiciansWhoRejected().add(musician);
        musician.getPendingProjects().remove(project);

        projectRepository.save(project);
        // Optional: Send notification to inspector?
    }

    public List<Musician> getAvailableMusicians(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<Musician> allMusicians = (List<Musician>) musicianRepository.findAll();

        return allMusicians.stream()
                .filter(musician -> !project.getProjectMembers().contains(musician) &&
                        !project.getMusiciansWhoRejected().contains(musician) &&
                        !project.getInvited().contains(musician))
                .collect(Collectors.toList());
    }

    public List<Project> getOngoingProjects() {
        LocalDate today = LocalDate.now();
        return projectRepository.findByStartDateBeforeAndEndDateAfter(today, today.minusDays(1));
    }

    public List<Project> getFutureProjects() {
        LocalDate today = LocalDate.now();
        return projectRepository.findByStartDateAfter(today);
    }

    public List<Project> getArchivedProjects() {
        LocalDate today = LocalDate.now();
        return projectRepository.findByEndDateBefore(today);
    }

    @Transactional
    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);

    }

    @Transactional
    public void updateProject(Long id, ProjectDTO dto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setMusicScores(dto.getMusicScores());
        project.setAgreementTemplate(dto.getAgreementTemplate());
    }
}