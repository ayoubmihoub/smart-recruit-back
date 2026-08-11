package com.irrigo.userservice.dto;

import lombok.Data;

@Data
public class AdminProfileUpdateRequest {

    private String firstName;
    private String lastName;
    private String email;
}