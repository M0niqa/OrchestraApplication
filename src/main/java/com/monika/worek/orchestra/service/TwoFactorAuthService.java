package com.monika.worek.orchestra.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TwoFactorAuthService {

    private final EmailService emailService;
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public TwoFactorAuthService(EmailService emailService) {
        this.emailService = emailService;
    }

    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();
        otpStorage.put(email, code);
        emailService.sendEmail(email, "Verification Code",
                "Please use the following code to complete your login: " + code);
    }

    public boolean verifyCode(String userEmail, String userEnteredCode) {
        String storedCode = otpStorage.get(userEmail);
        if (storedCode != null && storedCode.equals(userEnteredCode)) {
            otpStorage.remove(userEmail);
            return true;
        }
        return false;
    }
}
