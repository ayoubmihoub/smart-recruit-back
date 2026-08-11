package com.irrigo.userservice.dto;

import lombok.Data;

@Data
public class CandidateProfileUpdateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    
}