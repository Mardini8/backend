package com.PatientSystem.PatientSystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // ÄNDRAT: foreignId är nu String för att matcha HAPI FHIR ID-format
    // För PATIENT: "1", "2", etc (Patient ID från HAPI)
    // För DOCTOR/STAFF: "1", "2", etc (Practitioner ID från HAPI)
    private String foreignId;
}