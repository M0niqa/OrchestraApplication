package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.VerificationCode;
import com.monika.worek.orchestra.repository.VerificationCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @InjectMocks
    private TwoFactorAuthService twoFactorAuthService;

    @Test
    void createCode_whenCalled_thenDeletesOldCodesAndSavesNewCodeInOrder() {
        // given
        String email = "test@example.com";
        String code = "987654";
        int validityInMinutes = 10;

        InOrder inOrder = inOrder(verificationCodeRepository);
        ArgumentCaptor<VerificationCode> codeCaptor = ArgumentCaptor.forClass(VerificationCode.class);

        // when
        twoFactorAuthService.createCode(email, code, validityInMinutes);

        // then
        inOrder.verify(verificationCodeRepository, times(1)).deleteByEmail(email);
        inOrder.verify(verificationCodeRepository, times(1)).save(codeCaptor.capture());

        VerificationCode savedCode = codeCaptor.getValue();
        assertThat(savedCode.getEmail()).isEqualTo(email);
        assertThat(savedCode.getCode()).isEqualTo(code);

        assertThat(savedCode.getExpiryDate()).isAfter(LocalDateTime.now().plusMinutes(9));
        assertThat(savedCode.getExpiryDate()).isBefore(LocalDateTime.now().plusMinutes(11));
    }

    @Test
    void sendVerificationCode_whenCalled_thenShouldCreateAndEmailCode() {
        // given
        String email = "test@example.com";

        // when
        twoFactorAuthService.sendVerificationCode(email);

        // then
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);

        verify(verificationCodeRepository, times(1)).deleteByEmail(email);
        verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
        verify(emailService, times(1)).sendEmail(eq(email), eq("Verification Code"), codeCaptor.capture());
    }

    @Test
    void isCodeValid_whenCodeIsValidAndNotExpired_thenShouldReturnTrueAndDeleteCode() {
        // given
        String email = "test@example.com";
        String correctCode = "123456";
        VerificationCode storedCode = new VerificationCode();
        storedCode.setCode(correctCode);
        storedCode.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(verificationCodeRepository.findByEmail(email)).thenReturn(storedCode);

        // when
        boolean isValid = twoFactorAuthService.isCodeValid(email, correctCode);

        // then
        assertThat(isValid).isTrue();
        verify(verificationCodeRepository, times(1)).delete(storedCode);
    }

    @Test
    void isCodeValid_whenCodeIsIncorrect_thenShouldReturnFalse() {
        // given
        String email = "test@example.com";
        String correctCode = "123456";
        String incorrectCode = "654321";
        VerificationCode storedCode = new VerificationCode();
        storedCode.setCode(correctCode);
        storedCode.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(verificationCodeRepository.findByEmail(email)).thenReturn(storedCode);

        // when
        boolean isValid = twoFactorAuthService.isCodeValid(email, incorrectCode);

        // then
        assertThat(isValid).isFalse();
        verify(verificationCodeRepository, never()).delete(any());
    }

    @Test
    void isCodeValid_whenCodeIsExpired_thenShouldReturnFalse() {
        // given
        String email = "test@example.com";
        String correctCode = "123456";
        VerificationCode storedCode = new VerificationCode();
        storedCode.setCode(correctCode);
        storedCode.setExpiryDate(LocalDateTime.now().minusMinutes(5));

        when(verificationCodeRepository.findByEmail(email)).thenReturn(storedCode);

        // when
        boolean isValid = twoFactorAuthService.isCodeValid(email, correctCode);

        // then
        assertThat(isValid).isFalse();
        verify(verificationCodeRepository, never()).delete(any());

    }

    @Test
    void isCodeValid_whenNoCodeExists_thenShouldReturnFalse() {
        // given
        String email = "test@example.com";
        String enteredCode = "123456";
        when(verificationCodeRepository.findByEmail(email)).thenReturn(null);

        // when
        boolean isValid = twoFactorAuthService.isCodeValid(email, enteredCode);

        // then
        assertThat(isValid).isFalse();
        verify(verificationCodeRepository, never()).delete(any());
    }
}