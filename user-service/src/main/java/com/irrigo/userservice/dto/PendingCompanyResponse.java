package com.irrigo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingCompanyResponse {

    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String country;
    private String governorate;
    private String sector;
    private String registrationNumber;
    private String registrationDocumentUrl;
}