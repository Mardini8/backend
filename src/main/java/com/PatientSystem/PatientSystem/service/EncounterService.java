package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Encounter;
import com.PatientSystem.PatientSystem.repository.EncounterRepository;
import com.PatientSystem.PatientSystem.repository.LocationRepository;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import com.PatientSystem.PatientSystem.repository.PractitionerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EncounterService {

    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final LocationRepository locationRepository;

    public List<Encounter> getEncountersForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient not found: " + patientId);
        }
        return encounterRepository.findByPatientId(patientId);
    }

    public List<Encounter> getEncountersByPractitioner(Long practitionerId) {
        return encounterRepository.findByPractitionerId(practitionerId);
    }

    public List<Encounter> getEncountersByLocation(Long locationId) {
        return encounterRepository.findByLocationId(locationId);
    }

    public Encounter saveEncounter(Encounter encounter) {
        if (encounter.getPatientId() == null || !patientRepository.existsById(encounter.getPatientId())) {
            throw new EntityNotFoundException("Patient not found: " + encounter.getPatientId());
        }
        if (encounter.getPractitionerId() != null &&
                !practitionerRepository.existsById(encounter.getPractitionerId())) {
            throw new EntityNotFoundException("Practitioner not found: " + encounter.getPractitionerId());
        }
        if (encounter.getLocationId() != null &&
                !locationRepository.existsById(encounter.getLocationId())) {
            throw new EntityNotFoundException("Location not found: " + encounter.getLocationId());
        }
        return encounterRepository.save(encounter);
    }

    public Optional<Encounter> getEncounterById(Long id) {
        return encounterRepository.findById(id);
    }

    public void deleteEncounter(Long id) {
        encounterRepository.deleteById(id);
    }
}