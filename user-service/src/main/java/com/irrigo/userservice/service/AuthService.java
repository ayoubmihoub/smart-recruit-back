package com.irrigo.userservice.service;

import com.irrigo.userservice.dto.CandidateSignupRequest;
import com.irrigo.userservice.dto.CompanySignupRequest;
import com.irrigo.userservice.dto.SigninRequest;
import com.irrigo.userservice.entity.Company;
import com.irrigo.userservice.entity.CompanyStatus;
import com.irrigo.userservice.entity.Role;
import com.irrigo.userservice.entity.User;
import com.irrigo.userservice.repository.CandidateRepository;
import com.irrigo.userservice.repository.CompanyRepository;
import com.irrigo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.irrigo.userservice.entity.Candidate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final KeycloakService keycloakService;
    private final CandidateRepository candidateRepository;
    private final RestTemplate restTemplate;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret:}")
    private String clientSecret;

    public User signupCandidate(CandidateSignupRequest request) {

        String keycloakId = keycloakService.createUser(
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPassword(),
                "CANDIDATE"
        );

        User user = User.builder()
                .keycloakId(keycloakId)
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(Role.CANDIDATE)
                .build();

        User savedUser = userRepository.save(user);

        Candidate candidate = Candidate.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .user(savedUser)
                .build();

        candidateRepository.save(candidate);

        return savedUser;
    }

    public Company signupCompany(CompanySignupRequest request) {

        log.info("Starting company signup for email: {}", request.getEmail());

        try {

            String keycloakId = keycloakService.createUser(
                    request.getEmail(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPassword(),
                    "RECRUITER"
            );

            User user = User.builder()
                    .keycloakId(keycloakId)
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .role(Role.RECRUITER)
                    .build();

            userRepository.save(user);

            Company company = Company.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .name(request.getName())
                    .website(request.getWebsite())
                    .country(request.getCountry())
                    .governorate(request.getGovernorate())
                    .postalCode(request.getPostalCode())
                    .sector(request.getSector())
                    .registrationNumber(request.getRegistrationNumber())
                    .registrationDocumentUrl(request.getRegistrationDocumentUrl())
                    .companyStatus(CompanyStatus.PENDING_VERIFICATION)
                    .user(user)
                    .build();

            return companyRepository.save(company);

        } catch (Exception e) {

            log.error("Company signup failed for email: {}", request.getEmail());
            log.error("Error message: {}", e.getMessage(), e);

            throw e;
        }
    }

    public String signin(SigninRequest request) {


        String url = serverUrl
                + "/realms/"
                + realm
                + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("username", request.getEmail());
        body.add("password", request.getPassword());

        if (clientSecret != null && !clientSecret.isBlank()) {
            body.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        return response.getBody();
    }

}