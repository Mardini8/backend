package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Encounter;
import com.PatientSystem.PatientSystem.repository.EncounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EncounterService {

    private final EncounterRepository encounterRepository;

    public List<Encounter> getEncountersForPatient(Long patientId) {
        return encounterRepository.findByPatientId(patientId);
    }

    public List<Encounter> getEncountersByPractitioner(Long practitionerId) {
        return encounterRepository.findByPractitionerId(practitionerId);
    }

    public List<Encounter> getEncountersByLocation(Long locationId) {
        return encounterRepository.findByLocationId(locationId);
    }

    public Encounter saveEncounter(Encounter encounter) {
        return encounterRepository.save(encounter);
    }

    public Optional<Encounter> getEncounterById(Long id) {
        return encounterRepository.findById(id);
    }

    public void deleteEncounter(Long id) {
        encounterRepository.deleteById(id);
    }
}