package dev.dex.reddit.service;

import dev.dex.reddit.entity.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String email;
    @Value("${application.base-url}")
    private String baseUrl;

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        LOG.info("Email to " + toEmail + " send");
    }

    public void sendVerificationEmail(User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(email);
        helper.setTo(user.getEmail());
        helper.setSubject("Account Verification");

        String verifyUrl = baseUrl + "/api/v1/auth/verify?code=" + user.getVerificationCode();
        String content = "Hello " + user.getUsername() + ",<br>"
                        + "Please click the link below to verify your account:<br>"
                        + "<a href=" + verifyUrl + ">Verify account</a>";
        helper.setText(content, true);
        mailSender.send(message);
    }

    public void sendForgotPasswordEmail(User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(email);
        helper.setTo(user.getEmail());
        helper.setSubject("Forgotten password");

        String changePasswordUrl = baseUrl + "/api/v1/auth/forgot-password-redirect?code=" + user.getForgotPasswordCode();
        String content = "Change your password with the link below:<br>"
                + "<a href=" + changePasswordUrl + ">Change password</a>";
        helper.setText(content, true);
        mailSender.send(message);
    }
}
