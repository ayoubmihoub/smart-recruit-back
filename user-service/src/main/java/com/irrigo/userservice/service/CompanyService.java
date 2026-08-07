package com.irrigo.userservice.service;

import com.irrigo.userservice.entity.Company;
import com.irrigo.userservice.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company getCompany(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company updateCompany(Long id, Company company) {

        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        existing.setName(company.getName());
        existing.setEmail(company.getEmail());
        existing.setPhone(company.getPhone());
        existing.setWebsite(company.getWebsite());
        existing.setCountry(company.getCountry());
        existing.setGovernorate(company.getGovernorate());
        existing.setPostalCode(company.getPostalCode());
        existing.setSector(company.getSector());
        existing.setRegistrationNumber(company.getRegistrationNumber());
        existing.setRegistrationDocumentUrl(company.getRegistrationDocumentUrl());

        return companyRepository.save(existing);
    }

    public void deleteCompany(Long id) {

        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company not found");
        }

        companyRepository.deleteById(id);
    }
}