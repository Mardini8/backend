package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.PractitionerDTO;
import com.PatientSystem.PatientSystem.mapper.FhirMapper;
import com.PatientSystem.PatientSystem.service.HapiPractitionerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller för Practitioner - använder HAPI FHIR
 */
@RestController
@RequestMapping("/api/practitioners")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PractitionerController {

    private final HapiPractitionerService hapiPractitionerService;

    /**
     * Hämta alla practitioners från HAPI
     */
    @GetMapping
    public List<PractitionerDTO> getAllPractitioners() {
        return hapiPractitionerService.getAllPractitioners()
                .stream()
                .map(FhirMapper::practitionerToDTO)
                .toList();
    }

    /**
     * Hämta en specifik practitioner
     * id kan vara antingen numeriskt eller UUID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PractitionerDTO> getPractitionerById(@PathVariable String id) {
        return hapiPractitionerService.getPractitionerById(id)
                .map(FhirMapper::practitionerToDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Sök practitioner baserat på namn
     */
    @GetMapping("/search")
    public List<PractitionerDTO> searchPractitioner(@RequestParam String name) {
        return hapiPractitionerService.searchPractitionerByName(name)
                .stream()
                .map(FhirMapper::practitionerToDTO)
                .toList();
    }
}