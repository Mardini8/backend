package com.PatientSystem.PatientSystem.dto;

import java.time.LocalDate;

public record PatientDTO(
        Long id,
        String givenName,
        String familyName,
        LocalDate birthDate
) {}
