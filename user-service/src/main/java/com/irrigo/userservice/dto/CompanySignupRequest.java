package com.irrigo.userservice.dto;

import lombok.Data;

@Data
public class CompanySignupRequest {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String website;
    private String country;
    private String governorate;
    private String postalCode;
    private String sector;
    private String registrationNumber;
    private String registrationDocumentUrl;
}