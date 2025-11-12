package com.PatientSystem.PatientSystem.repository;

import com.PatientSystem.PatientSystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Hitta meddelanden baserat på patient personnummer (FHIR UUID)
    List<Message> findByPatientPersonnummerOrderBySentAtDesc(String patientPersonnummer);

    // Hitta meddelanden från en användare
    List<Message> findByFromUserIdOrderBySentAtDesc(Long fromUserId);

    // Hitta meddelanden till en användare
    List<Message> findByToUserIdOrderBySentAtDesc(Long toUserId);
}