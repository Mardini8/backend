package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Message;
import com.PatientSystem.PatientSystem.repository.MessageRepository;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import com.PatientSystem.PatientSystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientsRepository;


    public Message send(Message m) {
        if (m.getFromUserId() == null || !userRepository.existsById(m.getFromUserId())) {
            throw new EntityNotFoundException("Sender not found: " + m.getFromUserId());
        }
        if (m.getToUserId() == null || !userRepository.existsById(m.getToUserId())) {
            throw new EntityNotFoundException("Recipient not found: " + m.getToUserId());
        }
        if (m.getPatientId() == null || !patientsRepository.existsById(m.getPatientId())) {
            throw new EntityNotFoundException("Patient not found: " + m.getPatientId());
        }
        return messageRepository.save(m);
    }

    public List<Message> forPatient(Long patientId) {
        if (!patientsRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient not found: " + patientId);
        }
        return messageRepository.findByPatientIdOrderBySentAtDesc(patientId);
    }

    public List<Message> fromUser(Long userId) {
        return messageRepository.findByFromUserIdOrderBySentAtDesc(userId);
    }

    public List<Message> toUser(Long userId) {
        return messageRepository.findByToUserIdOrderBySentAtDesc(userId);
    }
}