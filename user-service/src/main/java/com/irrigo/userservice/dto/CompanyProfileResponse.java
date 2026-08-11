package com.irrigo.userservice.dto;

import com.irrigo.userservice.entity.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyProfileResponse {

    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String website;
    private String country;
    private String governorate;
    private String postalCode;
    private String sector;
    private String registrationNumber;
    private String registrationDocumentUrl;
    private String logoUrl;
    private CompanyStatus companyStatus;
}
