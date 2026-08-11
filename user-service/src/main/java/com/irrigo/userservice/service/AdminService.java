package com.irrigo.userservice.service;

import com.irrigo.userservice.dto.CandidateAdminResponse;
import com.irrigo.userservice.dto.CompanyAdminResponse;
import com.irrigo.userservice.entity.Candidate;
import com.irrigo.userservice.entity.Company;
import com.irrigo.userservice.entity.CompanyStatus;
import com.irrigo.userservice.entity.User;
import com.irrigo.userservice.repository.CandidateRepository;
import com.irrigo.userservice.repository.CompanyRepository;
import com.irrigo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CandidateRepository candidateRepo;
    private final CompanyRepository companyRepo;
    private final UserRepository userRepo;

    public List<CandidateAdminResponse> getCandidates() {

        List<Candidate> candidates =
                candidateRepo.findAll();

        return candidates.stream()
                .map(candidate -> {

                    User user = userRepo
                            .findById(candidate.getUser().getId())
                            .orElseThrow(() ->
                                    new RuntimeException("User not found"));

                    return new CandidateAdminResponse(
                            candidate.getId(),
                            candidate.getFirstName(),
                            candidate.getLastName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getRole()
                    );
                })
                .toList();
    }

    public List<CompanyAdminResponse> getCompanies() {

        List<Company> companies =
                companyRepo.findByCompanyStatus(
                        CompanyStatus.VERIFIED
                );

        return companies.stream()
                .map(company -> {

                    User user = userRepo
                            .findById(company.getUser().getId())
                            .orElseThrow(() ->
                                    new RuntimeException("User not found"));

                    return new CompanyAdminResponse(
                            company.getId(),
                            company.getName(),
                            company.getFirstName(),
                            company.getLastName(),
                            user.getEmail(),
                            user.getPhone(),
                            company.getWebsite(),
                            company.getCountry(),
                            company.getGovernorate(),
                            company.getSector(),
                            company.getRegistrationNumber(),
                            company.getCompanyStatus(),
                            user.getRole()
                    );
                })
                .toList();
    }
}