package com.project.ridex_backend.email;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
