package com.irrigo.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keycloakId;

    private String name;

    private String email;

    private String phone;

    private String website;

    private String country;

    private String governorate;

    private String postalCode;

    @Column(unique = true)
    private String registrationNumber;

    private String registrationDocumentUrl;

    private String logoUrl;

    private String sector;

    @Enumerated(EnumType.STRING)
    private CompanyStatus companyStatus;
}