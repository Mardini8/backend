package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.EncounterDTO;
import com.PatientSystem.PatientSystem.mapper.FhirMapper;
import com.PatientSystem.PatientSystem.service.HapiEncounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller för Encounter - använder HAPI FHIR
 */
@RestController
@RequestMapping("/api/v1/clinical/encounters")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class EncounterController {

    private final HapiEncounterService hapiEncounterService;

    /**
     * Hämta encounters för en specifik patient från HAPI
     * patientId kan vara antingen numeriskt (1, 2, 3) eller UUID
     */
    @GetMapping("/patient/{patientId}")
    public List<EncounterDTO> getEncountersForPatient(@PathVariable String patientId) {
        return hapiEncounterService.getEncountersForPatient(patientId)
                .stream()
                .map(FhirMapper::encounterToDTO)
                .toList();
    }

    /**
     * Hämta en specifik encounter
     */
    @GetMapping("/{id}")
    public ResponseEntity<EncounterDTO> getEncounterById(@PathVariable String id) {
        return hapiEncounterService.getEncounterById(id)
                .map(FhirMapper::encounterToDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}