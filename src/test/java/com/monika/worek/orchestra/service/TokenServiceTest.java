package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.Token;
import com.monika.worek.orchestra.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void createToken_whenCalled_thenShouldSaveAndReturnNewToken() {
        // given
        String email = "test@example.com";
        int validityInMinutes = 30;
        UUID fixedUuid = UUID.fromString("a1b2c3d4-e5f6-4a3b-8c2d-1f2e3d4c5b6a");

        try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {
            mockedUuid.when(UUID::randomUUID).thenReturn(fixedUuid);

            ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);

            // when
            Token createdToken = tokenService.createToken(email, validityInMinutes);

            // then
            verify(tokenRepository, times(1)).save(tokenCaptor.capture());
            Token savedToken = tokenCaptor.getValue();

            assertThat(createdToken).isNotNull();
            assertThat(createdToken.getToken()).isEqualTo(fixedUuid.toString());

            assertThat(savedToken.getEmail()).isEqualTo(email);
            assertThat(savedToken.getToken()).isEqualTo(fixedUuid.toString());
            assertThat(savedToken.getExpiryDate()).isAfter(LocalDateTime.now().plusMinutes(29));
            assertThat(savedToken.getExpiryDate()).isBefore(LocalDateTime.now().plusMinutes(31));
        }
    }

    @Test
    void getEmailForToken_whenTokenIsValid_thenShouldReturnEmail() {
        // given
        String tokenString = "valid-token";
        String expectedEmail = "test@example.com";
        Token token = new Token();
        token.setEmail(expectedEmail);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        // when
        String actualEmail = tokenService.getEmailForToken(tokenString);

        // then
        assertThat(actualEmail).isEqualTo(expectedEmail);
    }

    @Test
    void getEmailForToken_whenTokenIsExpired_thenShouldReturnNull() {
        // given
        String tokenString = "expired-token";
        Token token = new Token();
        token.setEmail("test@example.com");
        token.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        // when
        String actualEmail = tokenService.getEmailForToken(tokenString);

        // then
        assertThat(actualEmail).isNull();
    }

    @Test
    void getEmailForToken_whenTokenDoesNotExist_thenShouldReturnNull() {
        // given
        String tokenString = "nonexistent-token";
        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());

        // when
        String actualEmail = tokenService.getEmailForToken(tokenString);

        // then
        assertThat(actualEmail).isNull();
    }

    @Test
    void invalidateToken_whenCalled_thenShouldInvokeRepositoryDelete() {
        // given
        String tokenString = "token-to-delete";

        // when
        tokenService.invalidateToken(tokenString);

        // then
        verify(tokenRepository, times(1)).deleteByToken(tokenString);

    }
}