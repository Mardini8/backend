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
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4000)
    private String content;

    private LocalDateTime sentAt = LocalDateTime.now();

    // User IDs (för att hitta User-objekt i databasen)
    private Long fromUserId;
    private Long toUserId;

    // Patient personnummer (FHIR UUID från socialSecurityNumber)
    // T.ex. "dd256214-a911-bbc9-bc56-2976d2336c93"
    @Column(length = 255)
    private String patientPersonnummer;
}