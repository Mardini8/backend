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
     */
    @PostMapping
    public ResponseEntity<EncounterDTO> createEncounter(@RequestBody CreateEncounterRequest request) {
        try {
            // Parse datum
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date startTime = sdf.parse(request.startTime());
            Date endTime = request.endTime() != null && !request.endTime().isEmpty()
                    ? sdf.parse(request.endTime())
                    : null;

            // Skapa i HAPI
            org.hl7.fhir.r4.model.Encounter encounter = hapiEncounterService.createEncounter(
                    request.patientPersonnummer(),
                    request.practitionerPersonnummer(),
                    startTime,
                    endTime
            );

            // Konvertera till DTO
            EncounterDTO dto = FhirMapper.encounterToDTO(encounter);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
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