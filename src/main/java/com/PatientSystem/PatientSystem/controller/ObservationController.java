package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.ObservationDTO;
import com.PatientSystem.PatientSystem.mapper.FhirMapper;
import com.PatientSystem.PatientSystem.service.HapiObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller för Observation - använder HAPI FHIR
 */
@RestController
@RequestMapping("/api/v1/clinical/observations")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ObservationController {

    private final HapiObservationService hapiObservationService;

    /**
     * Hämta observations för en specifik patient från HAPI
     * patientId kan vara antingen numeriskt (1, 2, 3) eller UUID
     */
    @GetMapping("/patient/{patientId}")
    public List<ObservationDTO> getObservationsForPatient(@PathVariable String patientId) {
        return hapiObservationService.getObservationsForPatient(patientId)
                .stream()
                .map(FhirMapper::observationToDTO)
                .toList();
    }

    /**
     * Hämta en specifik observation
     */
    @GetMapping("/{id}")
    public ResponseEntity<ObservationDTO> getObservationById(@PathVariable String id) {
        return hapiObservationService.getObservationById(id)
                .map(FhirMapper::observationToDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}