package com.irrigo.userservice.controller;

import com.irrigo.userservice.dto.CandidateAdminResponse;
import com.irrigo.userservice.dto.CompanyAdminResponse;
import com.irrigo.userservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminServ;

    @GetMapping("/candidates")
    public List<CandidateAdminResponse> getCandidates() {

        return adminServ.getCandidates();
    }

    @GetMapping("/companies")
    public List<CompanyAdminResponse> getCompanies() {

        return adminServ.getCompanies();
    }
}