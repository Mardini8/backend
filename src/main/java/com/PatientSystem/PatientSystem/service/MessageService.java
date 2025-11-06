package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Message;
import com.PatientSystem.PatientSystem.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository repository;
    public MessageService(MessageRepository repo){ this.repository = repo; }
    public Message send(Message m){ return repository.save(m); }
    public List<Message> forPatient(Long patientId){ return repository.findByPatientIdOrderBySentAtDesc(patientId); }
}
