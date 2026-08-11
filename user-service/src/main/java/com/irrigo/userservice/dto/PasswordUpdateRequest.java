package com.irrigo.userservice.dto;

import lombok.Data;

@Data
public class PasswordUpdateRequest {

    private String currentPassword;
    private String newPassword;
}