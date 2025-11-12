package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.MessageDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Message;
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

    /**
     * Hämta meddelanden för en patient (via personnummer)
     * @param patientPersonnummer FHIR UUID (socialSecurityNumber)
     */
    @GetMapping("/patient/{patientPersonnummer}")
    public List<MessageDTO> forPatient(@PathVariable String patientPersonnummer) {
        return service.forPatient(patientPersonnummer)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    /**
     * Hämta meddelanden från en användare
     */
    @GetMapping("/from-user/{userId}")
    public List<MessageDTO> fromUser(@PathVariable Long userId) {
        return service.fromUser(userId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    /**
     * Hämta meddelanden till en användare
     */
    @GetMapping("/to-user/{userId}")
    public List<MessageDTO> toUser(@PathVariable Long userId) {
        return service.toUser(userId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    /**
     * Skicka ett meddelande
     */
    @PostMapping
    public ResponseEntity<MessageDTO> send(@RequestBody MessageDTO dto) {
        Message message = ApiMapper.toEntity(dto);
        Message saved = service.send(message);

        return ResponseEntity
                .created(URI.create("/api/v1/messages/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }
}