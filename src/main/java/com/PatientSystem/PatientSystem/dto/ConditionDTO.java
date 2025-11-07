package com.PatientSystem.PatientSystem.dto;

import java.time.LocalDate;

public record ConditionDTO(
        Long id,
        Long patientId,
        Long practitionerId,   // practitioner som registrerat diagnosen
        String code,
        String display,
        LocalDate assertedDate
) {}
