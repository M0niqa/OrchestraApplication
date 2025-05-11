package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.auth.MusicianBasicDTOMapper;
import com.monika.worek.orchestra.auth.MusicianDTOMapper;
import com.monika.worek.orchestra.auth.ProjectBasicInfoDTOMapper;
import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class MusicianService {
    private final UserRepository userRepository;
    private final MusicianRepository musicianRepository;

    public MusicianService(UserRepository userRepository, MusicianRepository musicianRepository) {
        this.userRepository = userRepository;
        this.musicianRepository = musicianRepository;
    }

    public Musician getMusicianByEmail(String mail) {
        return musicianRepository.findByEmail(mail).orElseThrow(() -> new EntityNotFoundException("Musician not found"));
    }

    public MusicianDataDTO getMusicianDtoByEmail(String mail) {
        return MusicianDTOMapper.mapToDto(getMusicianByEmail(mail));
    }

    public MusicianBasicDTO getMusicianBasicDtoByEmail(String email) {
        return MusicianBasicDTOMapper.mapToDto(getMusicianByEmail(email));
    }

    public Musician getMusicianById(Long id) {
        return musicianRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Musician not found"));
    }

    public List<Musician> getAllMusiciansSortedBySurname() {
        List<Musician> musicians = (List<Musician>) musicianRepository.findAll();
        return musicians.stream().sorted(Comparator.comparing(Musician::getLastName)
                        .thenComparing(Musician::getFirstName)).toList();
    }

    @Transactional
    public void deleteMusicianById(Long id) {
        Musician musician = musicianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Musician not found"));

        for (Project project : musician.getAcceptedProjects()) {
            project.getProjectMembers().remove(musician);
        }

        for (Project project : musician.getRejectedProjects()) {
            project.getMusiciansWhoRejected().remove(musician);
        }

        for (Project project : musician.getPendingProjects()) {
            project.getInvited().remove(musician);
        }

        musician.getAcceptedProjects().clear();
        musician.getRejectedProjects().clear();
        musician.getPendingProjects().clear();

        musicianRepository.delete(musician);
    }

    @Transactional
    public void updateMusicianData(String currentEmail, MusicianDataDTO musicianDataDTO) {
        Musician musician = (Musician) userRepository.findByEmail(currentEmail).orElseThrow(() -> new EntityNotFoundException("Musician not found"));
        musician.setFirstName(musicianDataDTO.getFirstName());
        musician.setLastName(musicianDataDTO.getLastName());
        musician.setEmail(musicianDataDTO.getEmail());
        musician.setBirthdate(musicianDataDTO.getBirthdate());
        musician.setAddress(musicianDataDTO.getAddress());
        musician.setPesel(musicianDataDTO.getPesel());
        musician.setBankAccountNumber(musicianDataDTO.getBankAccountNumber());
        musician.setTaxOffice(musicianDataDTO.getTaxOffice());
    }

    private List<ProjectBasicInfoDTO> mapToDTO(List<Project> projects) {
        return projects.stream()
                .map(ProjectBasicInfoDTOMapper::mapToDto)
                .toList();
    }

    public List<ProjectBasicInfoDTO> getActiveAcceptedProjects(Long musicianId) {
        return mapToDTO(musicianRepository.findActiveAcceptedProjects(musicianId, LocalDate.now()));
    }

    public List<ProjectBasicInfoDTO> getActivePendingProjects(Long musicianId) {
        return mapToDTO(musicianRepository.findActivePendingProjects(musicianId, LocalDate.now()));
    }

    public List<ProjectBasicInfoDTO> getActiveRejectedProjects(Long musicianId) {
        return mapToDTO(musicianRepository.findActiveRejectedProjects(musicianId, LocalDate.now()));
    }

    public List<ProjectBasicInfoDTO> getArchivedAcceptedProjects(Long musicianId) {
        return mapToDTO(musicianRepository.findArchivedAcceptedProjects(musicianId, LocalDate.now()));
    }

    public List<ProjectBasicInfoDTO> getArchivedRejectedProjects(Long musicianId) {
        return mapToDTO(musicianRepository.findArchivedRejectedProjects(musicianId, LocalDate.now()));
    }

    private String maskPesel(String pesel) {
        if (!pesel.isBlank()) {
            return "*******" + pesel.substring(pesel.length()-3);
        }
        return "";
    }
}
