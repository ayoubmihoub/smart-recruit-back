package com.irrigo.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String name;

    private String website;

    private String country;

    private String governorate;

    private String postalCode;

    private String registrationNumber;

    private String registrationDocumentUrl;

    private String logoUrl;

    private String sector;

    @Enumerated(EnumType.STRING)
    private CompanyStatus companyStatus;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}