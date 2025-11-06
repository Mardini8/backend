package com.PatientSystem.PatientSystem.repository;

import com.PatientSystem.PatientSystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    List<Condition> findByPatientId(Long patientId);
}
