package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.ObservationDTO;
import com.PatientSystem.PatientSystem.mapper.FhirMapper;
import com.PatientSystem.PatientSystem.service.HapiObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    /**
     * Skapa en ny observation i HAPI FHIR
     */
    @PostMapping
    public ResponseEntity<ObservationDTO> createObservation(@RequestBody CreateObservationRequest request) {
        try {
            // Parse datum
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date effectiveDateTime = sdf.parse(request.effectiveDateTime());

            // Skapa i HAPI
            org.hl7.fhir.r4.model.Observation observation = hapiObservationService.createObservation(
                    request.patientPersonnummer(),
                    request.performerPersonnummer(),
                    request.description(),
                    request.value(),
                    request.unit(),
                    effectiveDateTime
            );

            // Konvertera till DTO
            ObservationDTO dto = FhirMapper.observationToDTO(observation);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Request för att skapa observation
     */
    public record CreateObservationRequest(
            String patientPersonnummer,      // FHIR UUID
            String performerPersonnummer,    // FHIR UUID (valfri)
            String description,
            String value,                     // Värde (valfri)
            String unit,                      // Enhet (valfri)
            String effectiveDateTime          // Format: "yyyy-MM-dd'T'HH:mm"
    ) {}
}