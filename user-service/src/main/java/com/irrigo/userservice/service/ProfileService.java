package com.irrigo.userservice.service;

import com.irrigo.userservice.dto.AdminProfileResponse;
import com.irrigo.userservice.dto.AdminProfileUpdateRequest;
import com.irrigo.userservice.dto.CandidateProfileResponse;
import com.irrigo.userservice.dto.CandidateProfileUpdateRequest;
import com.irrigo.userservice.dto.CandidateProfileUpdateResponse;
import com.irrigo.userservice.dto.CompanyProfileResponse;
import com.irrigo.userservice.dto.CompanyProfileUpdateRequest;
import com.irrigo.userservice.dto.CompanyProfileUpdateResponse;
import com.irrigo.userservice.entity.Candidate;
import com.irrigo.userservice.entity.Company;
import com.irrigo.userservice.entity.Role;
import com.irrigo.userservice.entity.User;
import com.irrigo.userservice.repository.CandidateRepository;
import com.irrigo.userservice.repository.CompanyRepository;
import com.irrigo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import com.irrigo.userservice.dto.PasswordUpdateRequest;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepo;
    private final CandidateRepository candidateRepo;
    private final CompanyRepository companyRepo;
    private final KeycloakService keycloakServ;

    public CandidateProfileResponse getCandidateProfile(
            String keycloakId
    ) {

        User user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getRole() != Role.CANDIDATE) {
            throw new RuntimeException("User is not a candidate");
        }

        Candidate candidate = candidateRepo
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Candidate not found"));

        return new CandidateProfileResponse(
                candidate.getId(),
                candidate.getFirstName(),
                candidate.getLastName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    public CompanyProfileResponse getCompanyProfile(
            String keycloakId
    ) {

        User user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getRole() != Role.RECRUITER) {
            throw new RuntimeException("User is not a recruiter");
        }

        Company company = companyRepo
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        return new CompanyProfileResponse(
                company.getId(),
                company.getName(),
                user.getEmail(),
                user.getPhone(),
                company.getWebsite(),
                company.getCountry(),
                company.getGovernorate(),
                company.getPostalCode(),
                company.getSector(),
                company.getRegistrationNumber(),
                company.getRegistrationDocumentUrl(),
                company.getLogoUrl(),
                company.getFirstName(),
                company.getLastName(),
                company.getCompanyStatus()
        );
    }

    public CandidateProfileUpdateResponse updateCandidateProfile(
            String keycloakId,
            CandidateProfileUpdateRequest request
    ) {

        User user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getRole() != Role.CANDIDATE) {
            throw new RuntimeException("User is not a candidate");
        }

        Candidate candidate = candidateRepo
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Candidate not found"));

        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        candidate.setFirstName(request.getFirstName());
        candidate.setLastName(request.getLastName());

        candidateRepo.save(candidate);
        User savedUser = userRepo.save(user);

        keycloakServ.updateUser(
                user.getKeycloakId(),
                savedUser.getEmail(),
                candidate.getFirstName(),
                candidate.getLastName()
        );

        return new CandidateProfileUpdateResponse(
                candidate.getId(),
                candidate.getFirstName(),
                candidate.getLastName(),
                savedUser.getEmail(),
                savedUser.getPhone()
        );
    }

    public CompanyProfileUpdateResponse updateCompanyProfile(
            String keycloakId,
            CompanyProfileUpdateRequest request
    ) {

        User user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getRole() != Role.RECRUITER) {
            throw new RuntimeException("User is not a recruiter");
        }

        Company company = companyRepo
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        company.setName(request.getName());
        company.setFirstName(request.getFirstName());
        company.setLastName(request.getLastName());
        company.setWebsite(request.getWebsite());
        company.setCountry(request.getCountry());
        company.setGovernorate(request.getGovernorate());
        company.setPostalCode(request.getPostalCode());
        company.setSector(request.getSector());
        company.setRegistrationNumber(
                request.getRegistrationNumber()
        );
        company.setRegistrationDocumentUrl(
                request.getRegistrationDocumentUrl()
        );
        company.setLogoUrl(request.getLogoUrl());

        companyRepo.save(company);
        User savedUser = userRepo.save(user);

        keycloakServ.updateUser(
                user.getKeycloakId(),
                savedUser.getEmail(),
                company.getFirstName(),
                company.getLastName()
        );

        return new CompanyProfileUpdateResponse(
                company.getId(),
                company.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                company.getWebsite(),
                company.getCountry(),
                company.getGovernorate(),
                company.getPostalCode(),
                company.getSector(),
                company.getRegistrationNumber(),
                company.getRegistrationDocumentUrl(),
                company.getLogoUrl(),
                company.getCompanyStatus()
        );
    }

    public AdminProfileResponse getAdminProfile(
            String keycloakId
    ) {

        User dbUser = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (dbUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }

        UserRepresentation kcUser =
                keycloakServ.getUser(keycloakId);

        return new AdminProfileResponse(
                dbUser.getId().toString(),
                kcUser.getFirstName(),
                kcUser.getLastName(),
                kcUser.getEmail()
        );
    }

    public AdminProfileResponse updateAdminProfile(
            String keycloakId,
            AdminProfileUpdateRequest request
    ) {

        User dbUser = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (dbUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }

        dbUser.setEmail(request.getEmail());

        User savedUser = userRepo.save(dbUser);

        keycloakServ.updateUser(
                keycloakId,
                savedUser.getEmail(),
                request.getFirstName(),
                request.getLastName()
        );

        UserRepresentation kcUser =
                keycloakServ.getUser(keycloakId);

        return new AdminProfileResponse(
                savedUser.getId().toString(),
                kcUser.getFirstName(),
                kcUser.getLastName(),
                savedUser.getEmail()
        );
    }
    public void updatePassword(
            String keycloakId,
            PasswordUpdateRequest request
    ) {

        keycloakServ.updatePassword(
                keycloakId,
                request.getCurrentPassword(),
                request.getNewPassword()
        );
    }
}