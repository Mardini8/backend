package com.PatientSystem.PatientSystem.repository;

import com.PatientSystem.PatientSystem.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
