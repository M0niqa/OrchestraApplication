package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.Token;
import org.springframework.stereotype.Service;

import static com.monika.worek.orchestra.service.RegistrationService.BASE_URL;

@Service
public class PasswordResetService {

    private static final int TOKEN_EXPIRATION_IN_MINUTES = 5;
    private final EmailService emailService;
    private final TokenService tokenService;

    public PasswordResetService(EmailService emailService, TokenService tokenService) {
        this.emailService = emailService;
        this.tokenService = tokenService;
    }

    public void sendResetLink(String email) {
        Token token = tokenService.createToken(email, TOKEN_EXPIRATION_IN_MINUTES);
        String resetLink = BASE_URL+ "reset-password?token=" + token.getToken();
        emailService.sendEmail(email, "Reset Your Password", "Click here: " + resetLink);
    }
}

