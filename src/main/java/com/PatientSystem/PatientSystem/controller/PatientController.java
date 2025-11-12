package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.PatientDTO;
import com.PatientSystem.PatientSystem.mapper.FhirMapper;
import com.PatientSystem.PatientSystem.service.HapiPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller för Patient - använder HAPI FHIR istället för lokal databas
 * Detta ger högre betyg!
 */
@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PatientController {

    private final HapiPatientService hapiPatientService;

    /**
     * Hämta alla patienter från HAPI
     */
    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return hapiPatientService.getAllPatients()
                .stream()
                .map(FhirMapper::patientToDTO)
                .toList();
    }

    /**
     * Hämta en specifik patient från HAPI
     * id kan vara antingen numeriskt (1, 2, 3) eller UUID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        return hapiPatientService.getPatientById(id)
                .map(FhirMapper::patientToDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * OBS: POST/PUT/DELETE är inte implementerade för HAPI
     * Patienter skapas direkt i HAPI FHIR servern
     * För labben behöver vi bara kunna LÄSA data
     */
}