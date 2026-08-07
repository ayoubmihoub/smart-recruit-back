package com.irrigo.userservice.repository;

import com.irrigo.userservice.entity.Company;
import com.irrigo.userservice.entity.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByEmail(String email);

    Optional<Company> findByCompanyStatus(CompanyStatus companyStatus);
}