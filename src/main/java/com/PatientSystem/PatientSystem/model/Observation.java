package com.PatientSystem.PatientSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Observation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;      // typ av observation, t.ex. "blood-pressure"
    private String valueText; // t.ex. "120/80"
    private LocalDateTime effectiveDateTime;

    private Long patientId;
    private Long performerId;
    private Long encounterId;
}
