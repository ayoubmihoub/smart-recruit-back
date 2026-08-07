package com.irrigo.userservice.service;

import com.irrigo.userservice.dto.CandidateSignupRequest;
import com.irrigo.userservice.dto.CompanySignupRequest;
import com.irrigo.userservice.dto.SigninRequest;
import com.irrigo.userservice.entity.Company;
import com.irrigo.userservice.entity.CompanyStatus;
import com.irrigo.userservice.entity.Role;
import com.irrigo.userservice.entity.User;
import com.irrigo.userservice.repository.CompanyRepository;
import com.irrigo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final KeycloakService keycloakService;

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
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(Role.CANDIDATE)
                .build();

        return userRepository.save(user);
    }

    public Company signupCompany(CompanySignupRequest request) {

        String keycloakId = keycloakService.createUser(
                request.getEmail(),
                request.getName(),
                "",
                request.getPassword(),
                "RECRUITER"
        );

        Company company = Company.builder()
                .keycloakId(keycloakId)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .country(request.getCountry())
                .governorate(request.getGovernorate())
                .postalCode(request.getPostalCode())
                .sector(request.getSector())
                .registrationNumber(request.getRegistrationNumber())
                .registrationDocumentUrl(request.getRegistrationDocumentUrl())
                .companyStatus(CompanyStatus.PENDING_VERIFICATION)
                .build();

        return companyRepository.save(company);
    }

    public String signin(SigninRequest request) {

        RestTemplate restTemplate = new RestTemplate();

        String url = serverUrl
                + "/realms/"
                + realm
                + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("username", request.getEmail());
        body.add("password", request.getPassword());

        if (clientSecret != null && !clientSecret.isBlank()) {
            body.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }
}