package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.Token;
import com.monika.worek.orchestra.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token createToken(String email, int validityInMinutes) {
        String randomUUID = UUID.randomUUID().toString();
        Token token = new Token();
        token.setToken(randomUUID);
        token.setEmail(email);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(validityInMinutes));

        tokenRepository.save(token);
        return token;
    }

    public String getEmailForToken(String token) {
        Token resetToken = tokenRepository.findByToken(token)
                .orElse(null);

        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return null;
        }
        return resetToken.getEmail();
    }

    @Transactional
    public void invalidateToken(String token) {
        tokenRepository.deleteByToken(token);
    }

}
