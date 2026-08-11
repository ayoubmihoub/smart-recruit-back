package com.irrigo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CandidateProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}