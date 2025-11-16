package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.EncounterDTO;
import com.PatientSystem.PatientSystem.mapper.FhirMapper;
import com.PatientSystem.PatientSystem.service.HapiEncounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Controller för Encounter - använder HAPI FHIR
 * UPPDATERAD: Bättre datetime-hantering och felmeddelanden
 */
@RestController
@RequestMapping("/api/v1/clinical/encounters")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class EncounterController {

    private final HapiEncounterService hapiEncounterService;

    /**
     * Hämta encounters för en specifik patient från HAPI
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

    /**
     * Skapa ett nytt encounter (besök) i HAPI FHIR
     * UPPDATERAD: Bättre datetime-parsing och felhantering
     */
    @PostMapping
    public ResponseEntity<String> createEncounter(@RequestBody CreateEncounterRequest request) {
        try {
            System.out.println("=== SKAPAR ENCOUNTER ===");
            System.out.println("Patient: " + request.patientPersonnummer());
            System.out.println("Practitioner: " + request.practitionerPersonnummer());
            System.out.println("StartTime (från frontend): " + request.startTime());
            System.out.println("EndTime (från frontend): " + request.endTime());

            // Parse datum - VIKTIGT: Hantera datetime-local format från HTML input
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));

            Date startTime;
            try {
                startTime = sdf.parse(request.startTime());
                System.out.println("Parsed startTime: " + startTime);
            } catch (Exception e) {
                System.err.println("Kunde inte parsa startTime: " + request.startTime());
                return ResponseEntity.badRequest().body("Ogiltigt startTime-format. Använd: yyyy-MM-dd'T'HH:mm");
            }

            Date endTime = null;
            if (request.endTime() != null && !request.endTime().isEmpty()) {
                try {
                    endTime = sdf.parse(request.endTime());
                    System.out.println("Parsed endTime: " + endTime);
                } catch (Exception e) {
                    System.err.println("Kunde inte parsa endTime: " + request.endTime());
                    return ResponseEntity.badRequest().body("Ogiltigt endTime-format. Använd: yyyy-MM-dd'T'HH:mm");
                }
            }

            // Validera patient UUID
            if (request.patientPersonnummer() == null || request.patientPersonnummer().isEmpty()) {
                return ResponseEntity.badRequest().body("Patient personnummer saknas");
            }

            // Skapa i HAPI
            org.hl7.fhir.r4.model.Encounter encounter = hapiEncounterService.createEncounter(
                    request.patientPersonnummer(),
                    request.practitionerPersonnummer(),
                    startTime,
                    endTime
            );

            System.out.println("✓ Encounter skapat med ID: " + encounter.getIdElement().getIdPart());

            // Konvertera till DTO
            EncounterDTO dto = FhirMapper.encounterToDTO(encounter);

            return ResponseEntity.ok("Encounter skapat: " + encounter.getIdElement().getIdPart());
        } catch (Exception e) {
            System.err.println("=== FEL VID SKAPANDE AV ENCOUNTER ===");
            System.err.println("Felmeddelande: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Kunde inte skapa encounter: " + e.getMessage());
        }
    }

    /**
     * Request för att skapa encounter
     */
    public record CreateEncounterRequest(
            String patientPersonnummer,      // FHIR UUID
            String practitionerPersonnummer, // FHIR UUID (valfri)
            String startTime,                 // Format: "yyyy-MM-dd'T'HH:mm"
            String endTime                    // Format: "yyyy-MM-dd'T'HH:mm" (valfri)
    ) {}
}