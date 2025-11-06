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
    @ManyToOne(optional = false) private User fromUser;
    @ManyToOne(optional = false) private User toUser;

    @ManyToOne(optional = false) private Patient patient;

    @Column(length = 4000) private String content;
    private LocalDateTime sentAt = LocalDateTime.now();
}
