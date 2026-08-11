package com.irrigo.userservice.controller;

import com.irrigo.userservice.dto.CandidateProfileResponse;
import com.irrigo.userservice.dto.CandidateProfileUpdateRequest;
import com.irrigo.userservice.dto.CandidateProfileUpdateResponse;
import com.irrigo.userservice.dto.CompanyProfileResponse;
import com.irrigo.userservice.dto.CompanyProfileUpdateRequest;
import com.irrigo.userservice.dto.CompanyProfileUpdateResponse;
import com.irrigo.userservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.irrigo.userservice.dto.AdminProfileResponse;
import com.irrigo.userservice.dto.AdminProfileUpdateRequest;
import com.irrigo.userservice.dto.PasswordUpdateRequest;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileServ;

    @GetMapping("/candidate")
    public CandidateProfileResponse getCandidateProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {

        String keycloakId = jwt.getSubject();

        return profileServ.getCandidateProfile(keycloakId);
    }

    @GetMapping("/company")
    public CompanyProfileResponse getCompanyProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {

        String keycloakId = jwt.getSubject();

        return profileServ.getCompanyProfile(keycloakId);
    }

    @PutMapping("/candidate")
    public CandidateProfileUpdateResponse updateCandidateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CandidateProfileUpdateRequest request
    ) {

        String keycloakId = jwt.getSubject();

        return profileServ.updateCandidateProfile(
                keycloakId,
                request
        );
    }

    @PutMapping("/company")
    public CompanyProfileUpdateResponse updateCompanyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CompanyProfileUpdateRequest request
    ) {

        String keycloakId = jwt.getSubject();

        return profileServ.updateCompanyProfile(
                keycloakId,
                request
        );
    }
    @GetMapping("/admin")
    public AdminProfileResponse getAdminProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {

        String keycloakId = jwt.getSubject();

        return profileServ.getAdminProfile(keycloakId);
    }

    @PutMapping("/admin")
    public AdminProfileResponse updateAdminProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AdminProfileUpdateRequest request
    ) {

        String keycloakId = jwt.getSubject();

        return profileServ.updateAdminProfile(
                keycloakId,
                request
        );
    }
    @PutMapping("/password")
    public void updatePassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody PasswordUpdateRequest request
    ) {

        String keycloakId = jwt.getSubject();

        profileServ.updatePassword(
                keycloakId,
                request
        );
    }
}