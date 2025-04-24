package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.auth.MusicianDTOMapper;
import com.monika.worek.orchestra.dto.MusicianDTO;
import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Role;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.UserRepository;
import com.monika.worek.orchestra.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MusicianService {
    private final UserRepository userRepository;
    private final MusicianRepository musicianRepository;

    public MusicianService(UserRepository userRepository, MusicianRepository musicianRepository) {
        this.userRepository = userRepository;
        this.musicianRepository = musicianRepository;
    }

    public Optional<MusicianDTO> findMusicianByEmail(String mail) {
        return musicianRepository.findByEmail(mail).map(MusicianDTOMapper::map);
    }
//
//    @Transactional
//    public void deleteUser(String email) {
//        if (isCurrentUserAdmin())
//            userRepository.deleteByEmail(email);
//    }

    @Transactional
    public void updateUserData(String currentEmail, MusicianDTO userDTO) {
        Musician user = (Musician) userRepository.findByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthdate(userDTO.getBirthdate());
        user.setAddress(userDTO.getAddress());
        user.setPesel(userDTO.getPesel());
        user.setBankAccountNumber(userDTO.getBankAccountNumber());
        user.setTaxOffice(userDTO.getTaxOffice());
        user.setInstrument(userDTO.getInstrument());

        userRepository.save(user);
    }

    public List<Musician> getAllMusicians() {
        return (List<Musician>) musicianRepository.findAll();
    }
}
