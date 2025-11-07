package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.ConditionDTO;
import com.PatientSystem.PatientSystem.dto.EncounterDTO;
import com.PatientSystem.PatientSystem.dto.ObservationDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.*;
import com.PatientSystem.PatientSystem.repository.*;
import com.PatientSystem.PatientSystem.service.ObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clinical")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ObservationController {
    private final ObservationService service;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final EncounterRepository encounterRepository;
    private final ConditionRepository conditionRepository;
    private final LocationRepository locationRepository;

    // ========== OBSERVATIONS ==========

    @GetMapping("/patients/{id}/observations")
    public List<ObservationDTO> observations(@PathVariable Long id) {
        return service.observationsForPatient(id)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @PostMapping("/observations")
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
        Observation saved = service.saveObservation(observation);

        return ResponseEntity
                .created(URI.create("/api/v1/clinical/observations/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    // ========== CONDITIONS ==========

    @GetMapping("/patients/{id}/conditions")
    public List<ConditionDTO> conditions(@PathVariable Long id) {
        return conditionRepository.findByPatientId(id)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @PostMapping("/conditions")
    public ResponseEntity<ConditionDTO> createCondition(@RequestBody ConditionDTO dto) {
        // Validera att patient finns
        if (!patientRepository.existsById(dto.patientId())) {
            return ResponseEntity.badRequest().build();
        }

        // Validera recorder om angivet
        if (dto.practitionerId() != null && !practitionerRepository.existsById(dto.practitionerId())) {
            return ResponseEntity.badRequest().build();
        }

        Condition condition = ApiMapper.toEntity(dto);
        Condition saved = conditionRepository.save(condition);

        return ResponseEntity
                .created(URI.create("/api/v1/clinical/conditions/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    // ========== ENCOUNTERS ==========

    @GetMapping("/patients/{id}/encounters")
    public List<EncounterDTO> encounters(@PathVariable Long id) {
        return service.encountersForPatient(id)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @PostMapping("/encounters")
    public ResponseEntity<EncounterDTO> createEncounter(@RequestBody EncounterDTO dto) {
        // Validera att patient finns
        if (!patientRepository.existsById(dto.patientId())) {
            return ResponseEntity.badRequest().build();
        }

        // Validera practitioner om angivet
        if (dto.practitionerId() != null && !practitionerRepository.existsById(dto.practitionerId())) {
            return ResponseEntity.badRequest().build();
        }

        // Validera location om angivet
        if (dto.locationId() != null && !locationRepository.existsById(dto.locationId())) {
            return ResponseEntity.badRequest().build();
        }

        Encounter encounter = ApiMapper.toEntity(dto);
        Encounter saved = service.saveEncounter(encounter);

        return ResponseEntity
                .created(URI.create("/api/v1/clinical/encounters/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }
}