package com.PatientSystem.PatientSystem.dto;

import java.time.LocalDateTime;

public record ObservationDTO(
        Long id,
        Long patientId,
        Long performerId,     // practitioner (valfritt)
        Long encounterId,     // encounter (valfritt)
        String code,
        String valueText,
        LocalDateTime effectiveDateTime
) {}
