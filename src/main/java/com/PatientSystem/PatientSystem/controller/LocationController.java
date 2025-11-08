package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.LocationDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Location;
import com.PatientSystem.PatientSystem.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class LocationController {

    private final LocationService locationService;


    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO dto) {
        Location location = ApiMapper.toEntity(dto);
        Location saved = locationService.createLocation(location);

        return ResponseEntity
                .created(URI.create("/api/locations/" + saved.getId()))
                .body(ApiMapper.toDTO(saved));
    }

    @GetMapping
    public List<LocationDTO> getAllLocations() {
        return locationService.getAllLocations()
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }


    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getById(@PathVariable Long id) {
        return locationService.getLocationById(id)
                .map(ApiMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-city/{city}")
    public List<LocationDTO> getByCity(@PathVariable String city) {
        return locationService.getLocationsByCity(city)
                .stream()
                .map(ApiMapper::toDTO)
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(
            @PathVariable Long id,
            @RequestBody LocationDTO dto) {

        return locationService.getLocationById(id)
                .map(existing -> {
                    Location updated = ApiMapper.toEntity(dto);
                    updated.setId(id);
                    Location saved = locationService.updateLocation(updated);
                    return ResponseEntity.ok(ApiMapper.toDTO(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}