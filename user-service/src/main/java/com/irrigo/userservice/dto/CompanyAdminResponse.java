package com.irrigo.userservice.dto;

import com.irrigo.userservice.entity.CompanyStatus;
import com.irrigo.userservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyAdminResponse {

    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String website;
    private String country;
    private String governorate;
    private String sector;
    private String registrationNumber;
    private CompanyStatus companyStatus;
    private Role role;
}