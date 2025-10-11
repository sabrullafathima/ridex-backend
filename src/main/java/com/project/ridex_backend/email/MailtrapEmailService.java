package com.project.ridex_backend.email;

import com.project.ridex_backend.email.config.MailProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailtrapEmailService implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(MailtrapEmailService.class);

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("Processing send email to: {}", to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(mailProperties.getFrom());
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Error while sending email to {}: {}", to, e.getMessage());
        }
    }
}
