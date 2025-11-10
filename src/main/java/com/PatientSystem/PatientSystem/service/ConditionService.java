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

    /**
     * Validerar relations-ID:n mot repos med hjälp av DTO:n
     * (entiteten behöver inte ha motsvarande getXxxId()-metoder).
     */
    public Condition saveCondition(Condition condition, ConditionDTO dto) {
        Long patientId = dto.patientId();
        if (patientId == null || !patientRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient not found: " + patientId);
        }

        Long recorderId = dto.practitionerId();
        if (recorderId != null && !practitionerRepository.existsById(recorderId)) {
            throw new EntityNotFoundException("practitioner not found: " + recorderId);
        }

        // Entiteten har redan satta ID-fält via ApiMapper.toEntity(dto); vi behöver inte set:a relationer.
        return conditionRepository.save(condition);
    }

    public Optional<Condition> getConditionById(Long id) {
        return conditionRepository.findById(id);
    }

    public void deleteCondition(Long id) {
        conditionRepository.deleteById(id);
    }
}