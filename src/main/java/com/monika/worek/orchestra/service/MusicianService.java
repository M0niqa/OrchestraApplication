package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.auth.MusicianBasicDTOMapper;
import com.monika.worek.orchestra.auth.MusicianDTOMapper;
import com.monika.worek.orchestra.auth.UserBasicDTOMapper;
import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.MusicianDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MusicianService {
    private final UserRepository userRepository;
    private final MusicianRepository musicianRepository;

    public MusicianService(UserRepository userRepository, MusicianRepository musicianRepository) {
        this.userRepository = userRepository;
        this.musicianRepository = musicianRepository;
    }

    public Musician findMusicianByEmail(String mail) {
        return musicianRepository.findByEmail(mail).orElseThrow(() -> new IllegalArgumentException("Musician not found"));
    }

    public MusicianDTO getMusicianDtoByEmail(String mail) {
        return MusicianDTOMapper.mapToDto(findMusicianByEmail(mail));
    }

    public MusicianBasicDTO getMusicianBasicDtoByEmail(String email) {
        return MusicianBasicDTOMapper.mapToDto(findMusicianByEmail(email));
    }
//
//    @Transactional
//    public void deleteUser(String email) {
//        if (isCurrentUserAdmin())
//            userRepository.deleteByEmail(email);
//    }

    @Transactional
    public void updateUserData(String currentEmail, MusicianDTO userDTO) {
        Musician user = (Musician) userRepository.findByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("Musician not found"));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthdate(userDTO.getBirthdate());
        user.setAddress(userDTO.getAddress());
        user.setPesel(userDTO.getPesel());
        user.setBankAccountNumber(userDTO.getBankAccountNumber());
        user.setTaxOffice(userDTO.getTaxOffice());
        user.setInstrument(userDTO.getInstrument());
    }

    public List<Musician> getAllMusicians() {
        return (List<Musician>) musicianRepository.findAll();
    }
}
