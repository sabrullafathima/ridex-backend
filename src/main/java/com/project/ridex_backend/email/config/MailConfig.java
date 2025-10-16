package com.project.ridex_backend.email.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CustomMailProperties.class)
public class MailConfig {
    private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);
    private final CustomMailProperties customMailProperties;

    @Bean
    public JavaMailSender javaMailSender() {
        logger.info("MAIL_HOST: {}", customMailProperties.getHost());
        logger.info("MAIL_PORT: {}", customMailProperties.getPort());
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(customMailProperties.getHost());
        mailSender.setPort(customMailProperties.getPort());
        mailSender.setUsername(customMailProperties.getUsername());
        mailSender.setPassword(customMailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);

        return mailSender;
    }
}
