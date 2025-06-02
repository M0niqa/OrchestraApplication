package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.VerificationCode;
import com.monika.worek.orchestra.repository.VerificationCodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TwoFactorAuthService {

    private static final int CODE_EXPIRATION_IN_MINUTES = 5;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;


    public TwoFactorAuthService(EmailService emailService, VerificationCodeRepository verificationCodeRepository) {
        this.emailService = emailService;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void createCode(String email, String code, int validityInMinutes) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setEmail(email);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(validityInMinutes));

        verificationCodeRepository.save(verificationCode);
    }

    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();
        createCode(email, code, CODE_EXPIRATION_IN_MINUTES);
        emailService.sendEmail(email, "Verification Code",
                "Please use the following code to complete your login: " + code);
    }

    public boolean verifyCode(String userEmail, String userEnteredCode) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(userEmail);
        if (verificationCode.getCode() != null && verificationCode.getCode().equals(userEnteredCode)) {
            verificationCodeRepository.delete(verificationCode);
            return true;
        }
        return false;
    }
}
