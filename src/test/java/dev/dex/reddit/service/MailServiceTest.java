package dev.dex.reddit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    private MailService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MailService(mailSender);
    }

    @Test
    void canSendEmail() {
        // given
        String toEmail = "toEmail";
        String subject = "subject";
        String body = "body";

        // when
        underTest.sendEmail(toEmail, subject, body);

        // then
        ArgumentCaptor<SimpleMailMessage> messageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageArgumentCaptor.capture());
        SimpleMailMessage capturedMessage = messageArgumentCaptor.getValue();
        assertThat(capturedMessage.getTo()[0]).isEqualTo(toEmail);
        assertThat(capturedMessage.getSubject()).isEqualTo(subject);
        assertThat(capturedMessage.getText()).isEqualTo(body);
    }

}