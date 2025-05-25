package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Token;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.repository.UserRepository;
import com.monika.worek.orchestra.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private static final int TOKEN_EXPIRATION_IN_MINUTES = 120;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final TokenService tokenService;

    public RegistrationService(EmailService emailService, UserRepository userRepository, UserRoleRepository userRoleRepository, TokenService tokenService) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.tokenService = tokenService;
    }

    public void sendLink(String email) {
        Token token = tokenService.createToken(email, TOKEN_EXPIRATION_IN_MINUTES);
        String setupLink = "http://localhost:8080/set-password?token=" + token.getToken();
        emailService.sendEmail(email, "Complete Your Registration", "Click here to set your password and complete registration: " + setupLink);
    }

    @Transactional
    public void createMusician(MusicianRegisterDTO musicianRegisterDTO) {
        if (userRepository.findByEmail(musicianRegisterDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Musician already exists.");
        }

        Musician musician = new Musician();
        musician.setFirstName(musicianRegisterDTO.getFirstName());
        musician.setLastName(musicianRegisterDTO.getLastName());
        musician.setEmail(musicianRegisterDTO.getEmail());
        musician.setInstrument(musicianRegisterDTO.getInstrument());

        UserRole musicianRole = userRoleRepository.findByName("MUSICIAN")
                .orElseThrow(() -> new IllegalStateException("Role MUSICIAN not found"));

        musician.getRoles().add(musicianRole);
        userRepository.save(musician);
    }

}

