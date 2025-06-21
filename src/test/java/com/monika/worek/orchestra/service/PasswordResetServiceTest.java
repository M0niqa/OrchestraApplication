package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.Token;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void sendResetLink_whenCalled_thenCreatesTokenAndSendsEmailWithCorrectLink() {
        // given
        String email = "user@example.com";
        String tokenString = "a1b2c3d4-e5f6-4a3b-8c2d-1f2e3d4c5b6a";
        Token mockToken = new Token();
        mockToken.setToken(tokenString);

        when(tokenService.createToken(eq(email), anyInt())).thenReturn(mockToken);

        // when
        passwordResetService.sendResetLink(email);

        // then
        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService, times(1)).sendEmail(toCaptor.capture(), subjectCaptor.capture(), textCaptor.capture());

        assertThat(toCaptor.getValue()).isEqualTo(email);
        assertThat(subjectCaptor.getValue()).isEqualTo("Reset Your Password");

        String expectedLink = RegistrationService.BASE_URL + "reset-password?token=" + tokenString;
        assertThat(textCaptor.getValue()).contains(expectedLink);
    }
}