package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dtoMappers.MusicianBasicDTOMapper;
import com.monika.worek.orchestra.dtoMappers.MusicianDTOMapper;
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

import static com.monika.worek.orchestra.dtoMappers.ProjectBasicInfoDTOMapper.mapToListDTO;

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

    public boolean isDataMissing(Musician musician) {
        boolean hasPersonalData = isNotBlank(musician.getFirstName()) &&
                isNotBlank(musician.getLastName()) &&
                isNotBlank(musician.getAddress()) &&
                isNotBlank(musician.getPesel()) &&
                isNotBlank(musician.getBankAccountNumber());

        boolean hasBusinessData = isNotBlank(musician.getCompanyName()) &&
                isNotBlank(musician.getNip()) &&
                isNotBlank(musician.getBankAccountNumber());

        return !hasPersonalData && !hasBusinessData;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    public List<ProjectBasicInfoDTO> getActiveAcceptedProjectsDTOs(Long musicianId) {
        return mapToListDTO(musicianRepository.findActiveAcceptedProjects(musicianId, LocalDate.now()).stream().sorted().toList());
    }

    public List<ProjectBasicInfoDTO> getActivePendingProjectsDTOs(Long musicianId) {
        return mapToListDTO(musicianRepository.findActivePendingProjects(musicianId, LocalDate.now()).stream().sorted().toList());
    }

    public List<ProjectBasicInfoDTO> getActiveRejectedProjectsDTOs(Long musicianId) {
        return mapToListDTO(musicianRepository.findActiveRejectedProjects(musicianId, LocalDate.now()).stream().sorted().toList());
    }

    public List<ProjectBasicInfoDTO> getArchivedAcceptedProjectsDTOs(Long musicianId) {
        return mapToListDTO(musicianRepository.findArchivedAcceptedProjects(musicianId, LocalDate.now()).stream().sorted().toList());
    }

    public List<ProjectBasicInfoDTO> getArchivedRejectedProjectsDTOs(Long musicianId) {
        return mapToListDTO(musicianRepository.findArchivedRejectedProjects(musicianId, LocalDate.now()).stream().sorted().toList());
    }
}
