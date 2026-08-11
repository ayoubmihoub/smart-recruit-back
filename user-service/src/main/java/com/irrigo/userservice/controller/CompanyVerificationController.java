package com.irrigo.userservice.controller;

import com.irrigo.userservice.dto.PendingCompanyResponse;
import com.irrigo.userservice.service.CompanyVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/companies")
@RequiredArgsConstructor
public class CompanyVerificationController {

    private final CompanyVerificationService companyVerifServ;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PendingCompanyResponse> getPendingCompanies() {

        return companyVerifServ.getPendingCompanies();
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public void verifyCompany(
            @PathVariable Long id
    ) {

        companyVerifServ.verifyCompany(id);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public void rejectCompany(
            @PathVariable Long id
    ) {

        companyVerifServ.rejectCompany(id);
    }
}