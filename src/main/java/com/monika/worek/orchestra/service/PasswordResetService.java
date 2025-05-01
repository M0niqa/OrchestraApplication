package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.Token;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final EmailService emailService;
    private final TokenService tokenService;

    public PasswordResetService(EmailService emailService, TokenService tokenService) {
        this.emailService = emailService;
        this.tokenService = tokenService;
    }

    public void sendResetLink(String email) {
        Token token = tokenService.createToken(email);
        String resetLink = "http://localhost:8080/reset-password?token=" + token.getToken();
        emailService.sendEmail(email, "Reset Your Password", "Click here: " + resetLink);
    }
}

