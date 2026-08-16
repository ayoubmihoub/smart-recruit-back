package com.irrigo.userservice.service;

import com.irrigo.userservice.dto.CandidateSignupRequest;
import com.irrigo.userservice.dto.CompanySignupRequest;
import com.irrigo.userservice.dto.SigninRequest;
import com.irrigo.userservice.entity.Candidate;
import com.irrigo.userservice.entity.Company;
import com.irrigo.userservice.entity.CompanyStatus;
import com.irrigo.userservice.entity.Role;
import com.irrigo.userservice.entity.User;
import com.irrigo.userservice.repository.CandidateRepository;
import com.irrigo.userservice.repository.CompanyRepository;
import com.irrigo.userservice.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                authService,
                "serverUrl",
                "http://localhost:8081"
        );

        ReflectionTestUtils.setField(
                authService,
                "realm",
                "smart-recruit"
        );

        ReflectionTestUtils.setField(
                authService,
                "clientId",
                "smart-recruit-client"
        );

        ReflectionTestUtils.setField(
                authService,
                "clientSecret",
                ""
        );
    }

    @Test
    void signupCandidate_shouldCreateUserAndCandidate() {

        CandidateSignupRequest request = new CandidateSignupRequest();

        request.setEmail("candidate@test.com");
        request.setFirstName("Ali");
        request.setLastName("Ben Ali");
        request.setPassword("123456");
        request.setPhone("22123456");

        User savedUser = User.builder()
                .keycloakId("keycloak-123")
                .email("candidate@test.com")
                .phone("22123456")
                .role(Role.CANDIDATE)
                .build();

        when(keycloakService.createUser(
                "candidate@test.com",
                "Ali",
                "Ben Ali",
                "123456",
                "CANDIDATE"
        )).thenReturn("keycloak-123");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = authService.signupCandidate(request);

        assertNotNull(result);
        assertEquals("candidate@test.com", result.getEmail());
        assertEquals(Role.CANDIDATE, result.getRole());
        assertEquals("keycloak-123", result.getKeycloakId());

        verify(keycloakService).createUser(
                "candidate@test.com",
                "Ali",
                "Ben Ali",
                "123456",
                "CANDIDATE"
        );

        verify(userRepository).save(any(User.class));

        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void signupCompany_shouldCreateUserAndCompany() {

        CompanySignupRequest request = new CompanySignupRequest();

        request.setEmail("company@test.com");
        request.setFirstName("Mohamed");
        request.setLastName("Ben Ali");
        request.setPassword("123456");
        request.setPhone("22123456");
        request.setName("Test Company");
        request.setWebsite("https://test.com");
        request.setCountry("Tunisia");
        request.setGovernorate("Tunis");
        request.setPostalCode("1000");
        request.setSector("IT");
        request.setRegistrationNumber("REG123");
        request.setRegistrationDocumentUrl("document.pdf");

        User user = User.builder()
                .keycloakId("keycloak-456")
                .email("company@test.com")
                .phone("22123456")
                .role(Role.RECRUITER)
                .build();

        Company company = Company.builder()
                .firstName("Mohamed")
                .lastName("Ben Ali")
                .name("Test Company")
                .companyStatus(CompanyStatus.PENDING_VERIFICATION)
                .user(user)
                .build();

        when(keycloakService.createUser(
                "company@test.com",
                "Mohamed",
                "Ben Ali",
                "123456",
                "RECRUITER"
        )).thenReturn("keycloak-456");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(companyRepository.save(any(Company.class)))
                .thenReturn(company);

        Company result = authService.signupCompany(request);

        assertNotNull(result);
        assertEquals("Test Company", result.getName());
        assertEquals(
                CompanyStatus.PENDING_VERIFICATION,
                result.getCompanyStatus()
        );

        assertEquals(
                "company@test.com",
                result.getUser().getEmail()
        );

        assertEquals(
                Role.RECRUITER,
                result.getUser().getRole()
        );

        verify(keycloakService).createUser(
                "company@test.com",
                "Mohamed",
                "Ben Ali",
                "123456",
                "RECRUITER"
        );

        verify(userRepository).save(any(User.class));

        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void signupCandidate_shouldThrowExceptionWhenKeycloakFails() {

        CandidateSignupRequest request = new CandidateSignupRequest();

        request.setEmail("candidate@test.com");
        request.setFirstName("Ali");
        request.setLastName("Ben Ali");
        request.setPassword("123456");
        request.setPhone("22123456");

        when(keycloakService.createUser(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                eq("CANDIDATE")
        )).thenThrow(new RuntimeException("Keycloak error"));

        assertThrows(
                RuntimeException.class,
                () -> authService.signupCandidate(request)
        );

        verify(userRepository, never()).save(any(User.class));

        verify(candidateRepository, never()).save(any(Candidate.class));
    }
    @Test
    void signin_shouldReturnTokenResponse() {

        SigninRequest request = new SigninRequest();

        request.setEmail("test@test.com");
        request.setPassword("123456");

        ResponseEntity<String> response =
                ResponseEntity.ok("{\"access_token\":\"abc123\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(String.class)
        )).thenReturn(response);

        String result = authService.signin(request);

        assertNotNull(result);
        assertTrue(result.contains("access_token"));

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(String.class)
        );
    }
}