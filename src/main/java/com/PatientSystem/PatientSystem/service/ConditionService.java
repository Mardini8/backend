package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Condition;
import com.PatientSystem.PatientSystem.repository.ConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository conditionRepository;

    public List<Condition> getConditionsForPatient(Long patientId) {
        return conditionRepository.findByPatientId(patientId);
    }

    public Condition saveCondition(Condition condition) {
        return conditionRepository.save(condition);
    }

    public Optional<Condition> getConditionById(Long id) {
        return conditionRepository.findById(id);
    }

    public void deleteCondition(Long id) {
        conditionRepository.deleteById(id);
    }
}