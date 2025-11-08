package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Practitioner;
import com.PatientSystem.PatientSystem.repository.PractitionerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PractitionerService {

    private final PractitionerRepository practitionerRepository;

    public Practitioner createPractitioner(Practitioner practitioner) {
        return practitionerRepository.save(practitioner);
    }

    public Optional<Practitioner> getPractitionerById(Long id) {
        return practitionerRepository.findById(id);
    }

    public List<Practitioner> getAllPractitioners() {
        return practitionerRepository.findAll();
    }

    public List<Practitioner> getPractitionersByTitle(String title) {
        return practitionerRepository.findByTitle(title);
    }

    public Practitioner updatePractitioner(Practitioner practitioner) {
        return practitionerRepository.save(practitioner);
    }

    public void deletePractitioner(Long id) {
        practitionerRepository.deleteById(id);
    }
}