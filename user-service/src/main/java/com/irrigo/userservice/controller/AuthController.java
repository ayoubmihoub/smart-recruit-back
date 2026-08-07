package com.irrigo.userservice.controller;

import com.irrigo.userservice.dto.CandidateSignupRequest;
import com.irrigo.userservice.dto.CompanySignupRequest;
import com.irrigo.userservice.dto.SigninRequest;
import com.irrigo.userservice.service.AuthService;
import com.irrigo.userservice.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KeycloakService keycloakService;

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



}