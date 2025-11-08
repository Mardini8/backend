package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Message;
import com.PatientSystem.PatientSystem.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;

    public Message send(Message m) {
        return repository.save(m);
    }

    public List<Message> forPatient(Long patientId) {
        return repository.findByPatientIdOrderBySentAtDesc(patientId);
    }

    public List<Message> fromUser(Long userId) {
        return repository.findByFromUserIdOrderBySentAtDesc(userId);
    }

    public List<Message> toUser(Long userId) {
        return repository.findByToUserIdOrderBySentAtDesc(userId);
    }
}