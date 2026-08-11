package com.irrigo.userservice.dto;

import lombok.Data;

@Data
public class CompanyProfileUpdateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    private String name;
    private String website;
    private String country;
    private String governorate;
    private String postalCode;
    private String sector;
    private String registrationNumber;
    private String registrationDocumentUrl;
    private String logoUrl;
}