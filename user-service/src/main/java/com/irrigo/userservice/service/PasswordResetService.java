package com.irrigo.userservice.service;

import com.irrigo.userservice.entity.PasswordResetToken;
import com.irrigo.userservice.entity.User;
import com.irrigo.userservice.repository.PasswordResetTokenRepository;
import com.irrigo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final KeycloakService keycloakServ;
    private final EmailService emailServ;

    public void forgotPassword(String email) {

        User user = userRepo.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return;
        }

        tokenRepo.deleteByUserId(user.getId());

        SecureRandom random = new SecureRandom();

        byte[] bytes = new byte[32];

        random.nextBytes(bytes);

        String token =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);

        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .userId(user.getId())
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(15)
                        )
                        .build();

        tokenRepo.save(resetToken);

        emailServ.sendPasswordResetEmail(
                user.getEmail(),
                token
        );
    }

    public void resetPassword(
            String token,
            String newPassword
    ) {

        PasswordResetToken resetToken =
                tokenRepo.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid reset token"
                                )
                        );

        if (resetToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            tokenRepo.delete(resetToken);

            throw new RuntimeException(
                    "Reset token expired"
            );
        }

        User user =
                userRepo.findById(
                        resetToken.getUserId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        keycloakServ.resetPassword(
                user.getKeycloakId(),
                newPassword
        );

        tokenRepo.delete(resetToken);
    }
}