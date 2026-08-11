package com.irrigo.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(
            String email,
            String token
    ) {

        String link =
                frontendUrl
                        + "/reset-password?token="
                        + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Password reset");

        message.setText(
                "Hello,\n\n"
                        + "You requested a password reset.\n\n"
                        + "Click the following link to reset your password:\n\n"
                        + link
                        + "\n\n"
                        + "This link is valid for 15 minutes.\n\n"
                        + "If you did not request this, ignore this email."
        );

        mailSender.send(message);
    }
}