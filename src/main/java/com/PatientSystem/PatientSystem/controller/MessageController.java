package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.MessageDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Message;
import com.PatientSystem.PatientSystem.model.Patient;
import com.PatientSystem.PatientSystem.model.User;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import com.PatientSystem.PatientSystem.repository.UserRepository;
import com.PatientSystem.PatientSystem.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "http://localhost:3000") // CORS-konfiguration för frontend
@RequiredArgsConstructor
public class MessageController {
    private final MessageService service;
    private final UserRepository users;
    private final PatientRepository patients;

    @GetMapping("/patient/{patientId}")
    public List<MessageDTO> forPatient(@PathVariable Long patientId){
        return service.forPatient(patientId).stream().map(ApiMapper::toDTO).toList();
    }

    @PostMapping
    public ResponseEntity<MessageDTO> send(@RequestBody MessageDTO dto){
        User from = users.findById(dto.fromUserId()).orElseThrow();
        User to   = users.findById(dto.toUserId()).orElseThrow();
        Patient patient = patients.findById(dto.patientId()).orElseThrow();

        Message m = new Message();
        m.setFromUser(from);
        m.setToUser(to);
        m.setPatient(patient);
        m.setContent(dto.content());

        Message saved = service.send(m);
        return ResponseEntity.created(URI.create("/api/v1/messages/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }
}
