package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.OrganizationDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Organization;
import com.PatientSystem.PatientSystem.repository.LocationRepository;
import com.PatientSystem.PatientSystem.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final LocationRepository locationRepository;

    @PostMapping
    public ResponseEntity<OrganizationDTO> createOrganization(@RequestBody OrganizationDTO dto) {
        // Validera att location finns om locationId är angiven
        if (dto.locationId() != null && !locationRepository.existsById(dto.locationId())) {
            return ResponseEntity.badRequest().build();
        }

        Organization organization = ApiMapper.toEntity(dto);
        Organization saved = organizationService.createOrganization(organization);

        return ResponseEntity
                .created(URI.create("/api/organizations/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    @GetMapping
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationService.getAllOrganizations()
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id)
                .map(ApiMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> updateOrganization(
            @PathVariable Long id,
            @RequestBody OrganizationDTO dto) {

        // Validera att location finns om locationId är angiven
        if (dto.locationId() != null && !locationRepository.existsById(dto.locationId())) {
            return ResponseEntity.badRequest().build();
        }

        return organizationService.getOrganizationById(id)
                .map(existing -> {
                    Organization updated = ApiMapper.toEntity(dto);
                    updated.setId(id);
                    Organization saved = organizationService.updateOrganization(updated);
                    return ResponseEntity.ok(ApiMapper.toDTO(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}