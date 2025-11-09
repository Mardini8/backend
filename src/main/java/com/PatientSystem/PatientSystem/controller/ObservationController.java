package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.ObservationDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Observation;
import com.PatientSystem.PatientSystem.repository.EncounterRepository;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import com.PatientSystem.PatientSystem.repository.PractitionerRepository;
import com.PatientSystem.PatientSystem.service.ObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clinical/observations")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ObservationController {

    private final ObservationService observationService;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final EncounterRepository encounterRepository;

    @GetMapping("/patient/{patientId}")
    public List<ObservationDTO> getObservationsForPatient(@PathVariable Long patientId) {
        return observationService.getObservationsForPatient(patientId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/performer/{performerId}")
    public List<ObservationDTO> getObservationsByPerformer(@PathVariable Long performerId) {
        return observationService.getObservationsByPerformer(performerId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObservationDTO> getObservationById(@PathVariable Long id) {
        return observationService.getObservationById(id)
                .map(ApiMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ObservationDTO> createObservation(@RequestBody ObservationDTO dto) {
        // Validera att patient finns
        if (!patientRepository.existsById(dto.patientId())) {
            return ResponseEntity.badRequest().build();
        }

        // Validera performer om angivet
        if (dto.performerId() != null && !practitionerRepository.existsById(dto.performerId())) {
            return ResponseEntity.badRequest().build();
        }

        // Validera encounter om angivet
        if (dto.encounterId() != null && !encounterRepository.existsById(dto.encounterId())) {
            return ResponseEntity.badRequest().build();
        }

        Observation observation = ApiMapper.toEntity(dto);
        Observation saved = observationService.saveObservation(observation);

        return ResponseEntity
                .created(URI.create("/api/v1/clinical/observations/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObservationDTO> updateObservation(
            @PathVariable Long id,
            @RequestBody ObservationDTO dto) {

        return observationService.getObservationById(id)
                .map(existing -> {
                    Observation updated = ApiMapper.toEntity(dto);
                    updated.setId(id);
                    Observation saved = observationService.saveObservation(updated);
                    return ResponseEntity.ok(ApiMapper.toDTO(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObservation(@PathVariable Long id) {
        if (observationService.getObservationById(id).isPresent()) {
            observationService.deleteObservation(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}