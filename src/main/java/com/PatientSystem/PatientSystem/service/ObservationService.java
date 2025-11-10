package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.dto.ObservationDTO;
import com.PatientSystem.PatientSystem.model.Observation;
import com.PatientSystem.PatientSystem.repository.EncounterRepository;
import com.PatientSystem.PatientSystem.repository.ObservationRepository;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import com.PatientSystem.PatientSystem.repository.PractitionerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ObservationService {

    private final ObservationRepository observationRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final EncounterRepository encounterRepository;


    public List<Observation> getObservationsForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient not found: " + patientId);
        }
        return observationRepository.findByPatientId(patientId);
    }

    public List<Observation> getObservationsByPerformer(Long performerId) {
        if (!practitionerRepository.existsById(performerId)) {
            throw new EntityNotFoundException("Performer not found: " + performerId);
        }
        return observationRepository.findByPerformerId(performerId);
    }

    public Observation saveObservation(Observation observation) {
        if (observation.getPatientId() == null || !patientRepository.existsById(observation.getPatientId())) {
            throw new EntityNotFoundException("Patient not found: " + observation.getPatientId());
        }
        if (observation.getPerformerId() != null &&
                !practitionerRepository.existsById(observation.getPerformerId())) {
            throw new EntityNotFoundException("Practitioner not found: " + observation.getPerformerId());
        }
        if (observation.getEncounterId() != null &&
                !encounterRepository.existsById(observation.getEncounterId())) {
            throw new EntityNotFoundException("Encounter not found: " + observation.getEncounterId());
        }
        return observationRepository.save(observation);
    }

    public Optional<Observation> getObservationById(Long id) {
        return observationRepository.findById(id);
    }

    public void deleteObservation(Long id) {
        observationRepository.deleteById(id);
    }
}