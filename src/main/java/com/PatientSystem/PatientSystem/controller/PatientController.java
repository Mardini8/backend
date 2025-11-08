package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.PatientDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Patient;
import com.PatientSystem.PatientSystem.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO dto) {
        Patient patient = ApiMapper.toEntity(dto);
        Patient saved = patientService.createPatient(patient);
        return ResponseEntity
                .created(URI.create("/api/patients/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientService.getAllPatients()
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ApiMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ssn/{ssn}")
    public ResponseEntity<PatientDTO> getBySsn(@PathVariable String ssn) {
        return patientService.getPatientBySsn(ssn)
                .map(ApiMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @RequestBody PatientDTO dto) {

        return patientService.getPatientById(id)
                .map(existing -> {
                    Patient updated = ApiMapper.toEntity(dto);
                    updated.setId(id);
                    Patient saved = patientService.createPatient(updated);
                    return ResponseEntity.ok(ApiMapper.toDTO(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}