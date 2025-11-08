package com.PatientSystem.PatientSystem.repository;
import com.PatientSystem.PatientSystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    List<Encounter> findByPatientId(Long patientId);
    List<Encounter> findByPractitionerId(Long practitionerId);
    List<Encounter> findByLocationId(Long locationId);
}
