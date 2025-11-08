package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.PractitionerDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Practitioner;
import com.PatientSystem.PatientSystem.service.PractitionerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/practitioners")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PractitionerController {

    private final PractitionerService practitionerService;

    /**
     * POST /api/practitioners
     * Skapar en ny practitioner (Doctor eller Staff)
     */
    @PostMapping
    public ResponseEntity<PractitionerDTO> createPractitioner(@RequestBody PractitionerDTO dto) {
        // Validera title
        if (dto.title() != null &&
                !dto.title().equalsIgnoreCase("Doctor") &&
                !dto.title().equalsIgnoreCase("Staff")) {
            return ResponseEntity.badRequest().build();
        }

        Practitioner practitioner = ApiMapper.toEntity(dto);
        Practitioner saved = practitionerService.createPractitioner(practitioner);

        return ResponseEntity
                .created(URI.create("/api/practitioners/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    /**
     * GET /api/practitioners
     * Hämtar alla practitioners
     */
    @GetMapping
    public List<PractitionerDTO> getAllPractitioners() {
        return practitionerService.getAllPractitioners()
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    /**
     * GET /api/practitioners/{id}
     * Hämtar en specifik practitioner
     */
    @GetMapping("/{id}")
    public ResponseEntity<PractitionerDTO> getById(@PathVariable Long id) {
        return practitionerService.getPractitionerById(id)
                .map(ApiMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/practitioners/by-title/{title}
     * Hämtar alla practitioners med en specifik title (Doctor eller Staff)
     */
    @GetMapping("/by-title/{title}")
    public List<PractitionerDTO> getByTitle(@PathVariable String title) {
        return practitionerService.getPractitionersByTitle(title)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    /**
     * PUT /api/practitioners/{id}
     * Uppdaterar en practitioner
     */
    @PutMapping("/{id}")
    public ResponseEntity<PractitionerDTO> updatePractitioner(
            @PathVariable Long id,
            @RequestBody PractitionerDTO dto) {

        return practitionerService.getPractitionerById(id)
                .map(existing -> {
                    Practitioner updated = ApiMapper.toEntity(dto);
                    updated.setId(id);
                    Practitioner saved = practitionerService.updatePractitioner(updated);
                    return ResponseEntity.ok(ApiMapper.toDTO(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}