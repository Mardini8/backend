package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.ConditionDTO;
import com.PatientSystem.PatientSystem.mapper.FhirMapper;
import com.PatientSystem.PatientSystem.service.HapiConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller för Condition - använder HAPI FHIR
 */
@RestController
@RequestMapping("/api/v1/clinical/conditions")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ConditionController {

    private final HapiConditionService hapiConditionService;

    /**
     * Hämta conditions för en specifik patient från HAPI
     * patientId kan vara antingen numeriskt (1, 2, 3) eller UUID
     */
    @GetMapping("/patient/{patientId}")
    public List<ConditionDTO> getConditionsForPatient(@PathVariable String patientId) {
        return hapiConditionService.getConditionsForPatient(patientId)
                .stream()
                .map(FhirMapper::conditionToDTO)
                .toList();
    }

    /**
     * Hämta en specifik condition
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConditionDTO> getConditionById(@PathVariable String id) {
        return hapiConditionService.getConditionById(id)
                .map(FhirMapper::conditionToDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}