package com.irrigo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminProfileResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
}