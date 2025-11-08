package com.PatientSystem.PatientSystem.repository;
import com.PatientSystem.PatientSystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PractitionerRepository extends JpaRepository<Practitioner, Long> {
    List<Practitioner> findByTitle(String title);
    Optional<Practitioner> findBySocialSecurityNumber(String socialSecurityNumber);
}

