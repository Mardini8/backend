package com.PatientSystem.PatientSystem.dto;

import java.time.LocalDateTime;

public record MessageDTO(
        Long id,
        Long fromUserId,
        Long toUserId,
        Long patientId,
        String content,
        LocalDateTime sentAt
) {}
