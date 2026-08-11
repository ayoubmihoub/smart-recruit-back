package com.irrigo.userservice.controller;

import com.irrigo.userservice.dto.*;
import com.irrigo.userservice.service.AuthService;
import com.irrigo.userservice.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetServ;

    @PostMapping("/signup/candidate")
    public ResponseEntity<?> signupCandidate(
            @RequestBody CandidateSignupRequest request) {

        return ResponseEntity.ok(
                authService.signupCandidate(request)
        );
    }

    @PostMapping("/signup/company")
    public ResponseEntity<?> signupCompany(
            @RequestBody CompanySignupRequest request) {

        return ResponseEntity.ok(
                authService.signupCompany(request)
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(
            @RequestBody SigninRequest request) {

        return ResponseEntity.ok(
                authService.signin(request)
        );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {

        passwordResetServ.forgotPassword(
                request.getEmail()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "If an account exists with this email, a reset link has been sent."
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        passwordResetServ.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset successfully."
                )
        );
    }


}