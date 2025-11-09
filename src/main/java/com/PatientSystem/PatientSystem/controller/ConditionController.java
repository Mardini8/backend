package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.ConditionDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Condition;
import com.PatientSystem.PatientSystem.repository.PatientRepository;
import com.PatientSystem.PatientSystem.repository.PractitionerRepository;
import com.PatientSystem.PatientSystem.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clinical/conditions")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService conditionService;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;

    @GetMapping("/patient/{patientId}")
    public List<ConditionDTO> getConditionsForPatient(@PathVariable Long patientId) {
        return conditionService.getConditionsForPatient(patientId)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConditionDTO> getConditionById(@PathVariable Long id) {
        return conditionService.getConditionById(id)
                .map(ApiMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConditionDTO> createCondition(@RequestBody ConditionDTO dto) {
        // Validera att patient finns
        if (!patientRepository.existsById(dto.patientId())) {
            return ResponseEntity.badRequest().build();
        }

        // Validera practitioner om angivet
        if (dto.practitionerId() != null && !practitionerRepository.existsById(dto.practitionerId())) {
            return ResponseEntity.badRequest().build();
        }

        Condition condition = ApiMapper.toEntity(dto);
        Condition saved = conditionService.saveCondition(condition);

        return ResponseEntity
                .created(URI.create("/api/v1/clinical/conditions/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConditionDTO> updateCondition(
            @PathVariable Long id,
            @RequestBody ConditionDTO dto) {

        return conditionService.getConditionById(id)
                .map(existing -> {
                    Condition updated = ApiMapper.toEntity(dto);
                    updated.setId(id);
                    Condition saved = conditionService.saveCondition(updated);
                    return ResponseEntity.ok(ApiMapper.toDTO(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCondition(@PathVariable Long id) {
        if (conditionService.getConditionById(id).isPresent()) {
            conditionService.deleteCondition(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}