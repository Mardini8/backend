package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.MessageDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Message;
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
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService service;
    private final UserRepository users;
    private final PatientRepository patients;


    @GetMapping("/patient/{patientId}")
    public List<MessageDTO> forPatient(@PathVariable Long patientId) {
        return service.forPatient(patientId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/from-user/{userId}")
    public List<MessageDTO> fromUser(@PathVariable Long userId) {
        return service.fromUser(userId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/to-user/{userId}")
    public List<MessageDTO> toUser(@PathVariable Long userId) {
        return service.toUser(userId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<MessageDTO> send(@RequestBody MessageDTO dto) {
        // Validera att alla användare och patient finns
        if (!users.existsById(dto.fromUserId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!users.existsById(dto.toUserId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!patients.existsById(dto.patientId())) {
            return ResponseEntity.badRequest().build();
        }

        Message message = ApiMapper.toEntity(dto);
        Message saved = service.send(message);

        return ResponseEntity
                .created(URI.create("/api/v1/messages/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }
}