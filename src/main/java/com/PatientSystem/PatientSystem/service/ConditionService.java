package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.dto.ConditionDTO;
import com.PatientSystem.PatientSystem.model.Condition;
import com.PatientSystem.PatientSystem.repository.ConditionRepository;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import com.PatientSystem.PatientSystem.repository.PractitionerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository conditionRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;

    public List<Condition> getConditionsForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient not found: " + patientId);
        }
        return conditionRepository.findByPatientId(patientId);
    }

    public Condition saveCondition(Condition condition) {
        if (condition.getPatientId() == null || !patientRepository.existsById(condition.getPatientId())) {
            throw new EntityNotFoundException("Patient not found: " + condition.getPatientId());
        }
        if (condition.getPractitionerId() != null &&
                !practitionerRepository.existsById(condition.getPractitionerId())) {
            throw new EntityNotFoundException("Practitioner not found: " + condition.getPractitionerId());
        }
        return conditionRepository.save(condition);
    }

    public Optional<Condition> getConditionById(Long id) {
        return conditionRepository.findById(id);
    }

    public void deleteCondition(Long id) {
        conditionRepository.deleteById(id);
    }
}