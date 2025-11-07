package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.model.Message;
import com.PatientSystem.PatientSystem.service.MessageService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "http://localhost:3000") // CORS-konfiguration för frontend
public class MessageController {
    private final MessageService service;
    public MessageController(MessageService service){ this.service = service; }

    @GetMapping("/patient/{patientId}")
    public List<Message> forPatient(@PathVariable Long patientId){
        return service.forPatient(patientId);
    }

    @PostMapping
    public Message send(@RequestBody Message m){
        return service.send(m);
    }
}
