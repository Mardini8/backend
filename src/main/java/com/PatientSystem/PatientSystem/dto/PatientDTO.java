package com.PatientSystem.PatientSystem.dto;

public record PatientDTO(
        Long id,
        String givenName,
        String familyName,
        String birthDate
) {}
