package com.irrigo.userservice.service;

import com.irrigo.userservice.dto.PendingCompanyResponse;
import com.irrigo.userservice.entity.Company;
import com.irrigo.userservice.entity.CompanyStatus;
import com.irrigo.userservice.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyVerificationService {

    private final CompanyRepository companyRepo;

    public List<PendingCompanyResponse> getPendingCompanies() {

        List<Company> companies =
                companyRepo.findByCompanyStatus(
                        CompanyStatus.PENDING_VERIFICATION
                );

        return companies.stream()
                .map(company -> new PendingCompanyResponse(
                        company.getId(),
                        company.getName(),
                        company.getFirstName(),
                        company.getLastName(),
                        company.getUser().getEmail(),
                        company.getUser().getPhone(),
                        company.getCountry(),
                        company.getGovernorate(),
                        company.getSector(),
                        company.getRegistrationNumber(),
                        company.getRegistrationDocumentUrl()
                ))
                .toList();
    }

    public void verifyCompany(Long id) {

        Company company = companyRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        if (company.getCompanyStatus()
                != CompanyStatus.PENDING_VERIFICATION) {

            throw new RuntimeException(
                    "Company is not pending verification"
            );
        }

        company.setCompanyStatus(
                CompanyStatus.VERIFIED
        );

        companyRepo.save(company);
    }

    public void rejectCompany(Long id) {

        Company company = companyRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        if (company.getCompanyStatus()
                != CompanyStatus.PENDING_VERIFICATION) {

            throw new RuntimeException(
                    "Company is not pending verification"
            );
        }

        company.setCompanyStatus(
                CompanyStatus.REJECTED
        );

        companyRepo.save(company);
    }
}