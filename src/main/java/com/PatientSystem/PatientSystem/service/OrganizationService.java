package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Organization;
import com.PatientSystem.PatientSystem.repository.LocationRepository;
import com.PatientSystem.PatientSystem.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final LocationRepository locationRepository;


    public Organization createOrganization(Organization organization) {
        // Uppdatering: säkerställ att posten finns
        if (organization.getId() != null && !organizationRepository.existsById(organization.getId())) {
            throw new EntityNotFoundException("Organization not found: " + organization.getId());
        }
        return organizationRepository.save(organization);
    }

    public Optional<Organization> getOrganizationById(Long id) {
        return organizationRepository.findById(id);
    }

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    public Organization updateOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new EntityNotFoundException("Organization not found: " + id);
        }
        organizationRepository.deleteById(id);
    }
}