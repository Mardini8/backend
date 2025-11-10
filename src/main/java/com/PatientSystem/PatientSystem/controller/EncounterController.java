package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.EncounterDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Encounter;
import com.PatientSystem.PatientSystem.repository.LocationRepository;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import com.PatientSystem.PatientSystem.repository.PractitionerRepository;
import com.PatientSystem.PatientSystem.service.EncounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clinical/encounters")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class EncounterController {

    private final EncounterService encounterService;

    @GetMapping("/patient/{patientId}")
    public List<EncounterDTO> getEncountersForPatient(@PathVariable Long patientId) {
        return encounterService.getEncountersForPatient(patientId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/practitioner/{practitionerId}")
    public List<EncounterDTO> getEncountersByPractitioner(@PathVariable Long practitionerId) {
        return encounterService.getEncountersByPractitioner(practitionerId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/location/{locationId}")
    public List<EncounterDTO> getEncountersByLocation(@PathVariable Long locationId) {
        return encounterService.getEncountersByLocation(locationId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncounterDTO> getEncounterById(@PathVariable Long id) {
        return encounterService.getEncounterById(id)
                .map(ApiMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EncounterDTO> createEncounter(@RequestBody EncounterDTO dto) {
        Encounter entity = ApiMapper.toEntity(dto);
        Encounter saved = encounterService.saveEncounter(entity);

        return ResponseEntity
                .created(URI.create("/api/v1/clinical/encounters/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EncounterDTO> updateEncounter(
            @PathVariable Long id,
            @RequestBody EncounterDTO dto) {

        return encounterService.getEncounterById(id)
                .map(existing -> {
                    Encounter updated = ApiMapper.toEntity(dto);
                    updated.setId(id);
                    Encounter saved = encounterService.saveEncounter(updated);
                    return ResponseEntity.ok(ApiMapper.toDTO(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEncounter(@PathVariable Long id) {
        if (encounterService.getEncounterById(id).isPresent()) {
            encounterService.deleteEncounter(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}