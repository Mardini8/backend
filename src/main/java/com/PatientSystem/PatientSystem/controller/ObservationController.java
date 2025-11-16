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
 * UPPDATERAD: Bättre datetime-hantering och felmeddelanden
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
     * UPPDATERAD: Använder endast datum (yyyy-MM-dd) som diagnoser
     */
    @PostMapping
    public ResponseEntity<String> createObservation(@RequestBody CreateObservationRequest request) {
        try {
            System.out.println("=== SKAPAR OBSERVATION ===");
            System.out.println("Patient: " + request.patientPersonnummer());
            System.out.println("Performer: " + request.performerPersonnummer());
            System.out.println("Description: " + request.description());
            System.out.println("Value: " + request.value());
            System.out.println("Unit: " + request.unit());
            System.out.println("Date (från frontend): " + request.effectiveDate());

            // Parse datum - SAMMA FORMAT SOM DIAGNOSER: "yyyy-MM-dd"
            Date effectiveDate;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                effectiveDate = sdf.parse(request.effectiveDate());
                System.out.println("Parsed date: " + effectiveDate);
            } catch (Exception e) {
                System.err.println("Kunde inte parsa datum: " + request.effectiveDate());
                return ResponseEntity.badRequest().body("Ogiltigt datumformat. Använd: yyyy-MM-dd");
            }

            // Validera patient UUID
            if (request.patientPersonnummer() == null || request.patientPersonnummer().isEmpty()) {
                return ResponseEntity.badRequest().body("Patient personnummer saknas");
            }

            // Skapa i HAPI
            org.hl7.fhir.r4.model.Observation observation = hapiObservationService.createObservation(
                    request.patientPersonnummer(),
                    request.performerPersonnummer(),
                    request.description(),
                    request.value(),
                    request.unit(),
                    effectiveDate
            );

            System.out.println("✓ Observation skapad med ID: " + observation.getIdElement().getIdPart());

            // Konvertera till DTO
            ObservationDTO dto = FhirMapper.observationToDTO(observation);

            return ResponseEntity.ok("Observation skapad: " + observation.getIdElement().getIdPart());
        } catch (Exception e) {
            System.err.println("=== FEL VID SKAPANDE AV OBSERVATION ===");
            System.err.println("Felmeddelande: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Kunde inte skapa observation: " + e.getMessage());
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
            String effectiveDate              // Format: "yyyy-MM-dd" (SAMMA SOM DIAGNOSER)
    ) {}
}