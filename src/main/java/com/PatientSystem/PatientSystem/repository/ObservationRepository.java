package com.PatientSystem.PatientSystem.repository;

import com.PatientSystem.PatientSystem.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
}
