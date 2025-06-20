package com.monika.worek.orchestra.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_whenGivenValidParameters_thenShouldCallMailSender() {
        // given
        String to = "someone@example.com";
        String subject = "Test Subject";
        String text = "This is a test email.";

        // when
        emailService.sendEmail(to, subject, text);

        // then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).contains(to);
        assertThat(capturedMessage.getSubject()).isEqualTo(subject);
        assertThat(capturedMessage.getText()).isEqualTo(text);
    }

    @Test
    void sendEmail_whenMailSenderThrowsException_thenShouldCatchAndHandleException() {
        // given
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String text = "This is a test email.";

        doThrow(new MailException("Failed to connect to mail server") {}).when(mailSender).send(any(SimpleMailMessage.class));

        // when
        emailService.sendEmail(to, subject, text);

        // then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}