package com.PatientSystem.PatientSystem.dto;

public record LocationDTO(
        Long id,
        String name,
        String address,
        String city,
        String postalCode
) {}