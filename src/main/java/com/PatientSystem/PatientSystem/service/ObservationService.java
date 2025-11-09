package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Observation;
import com.PatientSystem.PatientSystem.repository.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ObservationService {

    private final ObservationRepository observationRepository;

    public List<Observation> getObservationsForPatient(Long patientId) {
        return observationRepository.findByPatientId(patientId);
    }

    public List<Observation> getObservationsByPerformer(Long performerId) {
        return observationRepository.findByPerformerId(performerId);
    }

    public Observation saveObservation(Observation observation) {
        return observationRepository.save(observation);
    }

    public Optional<Observation> getObservationById(Long id) {
        return observationRepository.findById(id);
    }

    public void deleteObservation(Long id) {
        observationRepository.deleteById(id);
    }
}