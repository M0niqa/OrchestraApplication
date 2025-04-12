package com.monika.worek.orchestra.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {

    private final Map<String, String> resetTokens = new ConcurrentHashMap<>();
    private final EmailService emailService;

    public PasswordResetService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendResetLink(String email) {
        String token = UUID.randomUUID().toString();
        resetTokens.put(token, email);
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendEmail(email, "Reset Your Password", "Click here: " + resetLink);
    }

    public String getEmailForToken(String token) {
        return resetTokens.get(token);
    }

    public void invalidateToken(String token) {
        resetTokens.remove(token);
    }
}

