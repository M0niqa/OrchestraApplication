package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Token;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.repository.UserRepository;
import com.monika.worek.orchestra.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private EmailService emailService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void sendLink_whenCalled_thenShouldCreateTokenAndSendEmailWithSetupLink() {
        // given
        String email = "new.musician@example.com";
        String tokenString = "a1b2c3d4-e5f6-4a3b-8c2d-1f2e3d4c5b6a";
        Token mockToken = new Token();
        mockToken.setToken(tokenString);

        when(tokenService.createToken(eq(email), anyInt())).thenReturn(mockToken);

        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        // when
        registrationService.sendLink(email);

        // then
        verify(emailService, times(1)).sendEmail(eq(email), eq("Complete Your Registration"), textCaptor.capture());

        String expectedLink = RegistrationService.BASE_URL + "set-password?token=" + tokenString;
        assertThat(textCaptor.getValue()).contains(expectedLink);
    }

    @Test
    void createMusician_whenUserDoesNotExist_thenShouldSaveNewMusicianWithRole() {
        // given
        MusicianRegisterDTO dto = new MusicianRegisterDTO("new@example.com", "Monika", "Stanko", null);
        UserRole musicianRole = new UserRole(1L, "MUSICIAN");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRoleRepository.findByName("MUSICIAN")).thenReturn(Optional.of(musicianRole));

        ArgumentCaptor<Musician> musicianCaptor = ArgumentCaptor.forClass(Musician.class);

        // when
        registrationService.createMusician(dto);

        // then
        verify(userRepository, times(1)).save(musicianCaptor.capture());
        Musician savedMusician = musicianCaptor.getValue();

        assertThat(savedMusician.getFirstName()).isEqualTo("Monika");
        assertThat(savedMusician.getLastName()).isEqualTo("Stanko");
        assertThat(savedMusician.getEmail()).isEqualTo("new@example.com");
        assertThat(savedMusician.getRoles()).contains(musicianRole);
    }

    @Test
    void createMusician_whenUserAlreadyExists_thenShouldThrowIllegalArgumentException() {
        // given
        MusicianRegisterDTO dto = new MusicianRegisterDTO("Monika", "Stanko", "existing@example.com", null);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new Musician()));

        // when
        // then
        assertThatThrownBy(() -> registrationService.createMusician(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Musician already exists.");

        verify(userRepository, never()).save(any());
    }

    @Test
    void createMusician_whenMusicianRoleNotFound_thenShouldThrowIllegalStateException() {
        // given
        MusicianRegisterDTO dto = new MusicianRegisterDTO("Monika", "Worek", "new@example.com", null);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRoleRepository.findByName("MUSICIAN")).thenReturn(Optional.empty()); // Role not found

        // when
        // then
        assertThatThrownBy(() -> registrationService.createMusician(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Role MUSICIAN not found");
    }
}