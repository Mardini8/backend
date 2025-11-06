package com.PatientSystem.PatientSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Condition {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;       // t.ex. ICD10
    private String display;    // beskrivning
    private LocalDate assertedDate;

    @ManyToOne(optional = false)
    private Patient patient;

    @ManyToOne
    private Practitioner recorder;
}
