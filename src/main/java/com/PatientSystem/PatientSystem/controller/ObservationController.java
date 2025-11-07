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
@CrossOrigin(origins = "http://localhost:3000") // CORS-konfiguration för frontend
@RequiredArgsConstructor

public class ObservationController {
    private final ObservationService service;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final EncounterRepository encounterRepository;

    @GetMapping("/patients/{id}/observations")
    public List<ObservationDTO> observations(@PathVariable Long id) {
        return service.observationsForPatient(id).stream().map(ApiMapper::toDTO).toList();
    }

    @PostMapping("/observations")
    public ResponseEntity<ObservationDTO> createObservation(@RequestBody ObservationDTO dto) {
        Patient patient = patientRepository.findById(dto.patientId()).orElseThrow();
        Practitioner performer = (dto.performerId()==null) ? null :
                practitionerRepository.findById(dto.performerId()).orElseThrow();
        Encounter enc = (dto.encounterId()==null) ? null :
                encounterRepository.findById(dto.encounterId()).orElseThrow();

        Observation saved = service.saveObservation(ApiMapper.toEntity(dto, patient, performer, enc));
        return ResponseEntity.created(URI.create("/api/v1/clinical/observations/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    private final ConditionRepository conditionRepository;

    @GetMapping("/patients/{id}/conditions")
    public List<ConditionDTO> conditions(@PathVariable Long id) {
        return conditionRepository.findByPatientId(id).stream().map(ApiMapper::toDTO).toList();
    }

    @PostMapping("/conditions")
    public ResponseEntity<ConditionDTO> createCondition(@RequestBody ConditionDTO dto) {
        Patient patient = patientRepository.findById(dto.patientId()).orElseThrow();
        Practitioner recorder = (dto.recorderId()==null) ? null :
                practitionerRepository.findById(dto.recorderId()).orElseThrow();
        Condition saved = conditionRepository.save(ApiMapper.toEntity(dto, patient, recorder));
        return ResponseEntity.created(URI.create("/api/v1/clinical/conditions/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    private final LocationRepository locationRepository;

    @GetMapping("/patients/{id}/encounters")
    public List<EncounterDTO> encounters(@PathVariable Long id) {
        return service.encountersForPatient(id).stream().map(ApiMapper::toDTO).toList();
    }
    @PostMapping("/encounters")
    public ResponseEntity<EncounterDTO> createEncounter(@RequestBody EncounterDTO dto) {
        Patient p = patientRepository.findById(dto.patientId()).orElseThrow();
        Practitioner pr = (dto.practitionerId()==null) ? null :
                practitionerRepository.findById(dto.practitionerId()).orElseThrow();
        Location l = (dto.locationId()==null) ? null :
                locationRepository.findById(dto.locationId()).orElseThrow();

        Encounter saved = service.saveEncounter(ApiMapper.toEntity(dto, p, pr, l));
        return ResponseEntity.created(URI.create("/api/v1/clinical/encounters/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }
}
