package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Patient;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient createPatient(Patient patient) {
        // Här kan vi lägga till valideringslogik, t.ex. kolla om personnumret redan finns.
        return patientRepository.save(patient);
    }

    public Optional<Patient> getPatientById(Long id) {
        // Här skulle vi i framtiden kunna lägga till auktoriseringskontroller:
        // if (användaren_är_läkare || användaren_är_patienten_själv) { ... }
        return patientRepository.findById(id);
    }

    public List<Patient> getAllPatients() {
        // Denna metod kräver stark auktorisering (Läkare/Personal) i en riktig applikation.
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientBySsn(String socialSecurityNumber) {
        return patientRepository.findBySocialSecurityNumber(socialSecurityNumber);
    }
}