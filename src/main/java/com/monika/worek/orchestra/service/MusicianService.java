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
    private final UserRoleRepository userRoleRepository;
    private final UserRoleRepository roleRepository;

    public MusicianService(UserRepository userRepository, MusicianRepository musicianRepository, UserRoleRepository userRoleRepository, UserRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.musicianRepository = musicianRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
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


//    @Transactional
//    public void register(MusicianRegisterDTO userDTO){
//        Musician musician = new Musician();
//        musician.setFirstName(userDTO.getFirstName());
//        musician.setLastName(userDTO.getLastName());
//        musician.setEmail(userDTO.getEmail());
//        musician.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userDTO.getPassword()));
//        Optional<UserRole> userRole = userRoleRepository.findByName("USER");
//        userRole.ifPresentOrElse(role -> musician.getRoles().add(role), NoSuchElementException::new);
//        musicianRepository.save(musician);
//    }

    @Transactional
    public void createMusician(MusicianRegisterDTO musicianRegisterDTO) {
        if (userRepository.findByEmail(musicianRegisterDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Musician already exists.");
        }

        Musician musician = new Musician();
        musician.setFirstName(musicianRegisterDTO.getFirstName());
        musician.setLastName(musicianRegisterDTO.getLastName());
        musician.setEmail(musicianRegisterDTO.getEmail());

        UserRole musicianRole = roleRepository.findByName(Role.MUSICIAN)
                .orElseThrow(() -> new IllegalStateException("Role MUSICIAN not found"));

        musician.getRoles().add(musicianRole);
        userRepository.save(musician);
    }

    @Transactional
    public void updateUserData(String currentEmail, MusicianDTO userDTO) {
        Musician user = (Musician) userRepository.findByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthdate(userDTO.getBirthdate());
        user.setAddress(userDTO.getAddress());
        user.setPesel(userDTO.getPesel());
        user.setTaxOffice(userDTO.getTaxOffice());

        userRepository.save(user);
    }

    public List<Musician> getAllMusicians() {
        return (List<Musician>) musicianRepository.findAll();
    }
}
