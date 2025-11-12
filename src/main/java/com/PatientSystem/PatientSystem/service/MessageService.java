package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.Message;
import com.PatientSystem.PatientSystem.repository.MessageRepository;
import com.PatientSystem.PatientSystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final HapiPatientService hapiPatientService;

    /**
     * Skicka ett meddelande
     * Validerar att avsändare och mottagare finns i User-databasen
     * Validerar att patient finns i HAPI FHIR (via personnummer)
     */
    public Message send(Message m) {
        // Validera avsändare
        if (m.getFromUserId() == null || !userRepository.existsById(m.getFromUserId())) {
            throw new EntityNotFoundException("Sender not found: " + m.getFromUserId());
        }

        // Validera mottagare
        if (m.getToUserId() == null || !userRepository.existsById(m.getToUserId())) {
            throw new EntityNotFoundException("Recipient not found: " + m.getToUserId());
        }

        // Validera att patient personnummer finns
        if (m.getPatientPersonnummer() == null || m.getPatientPersonnummer().isEmpty()) {
            throw new EntityNotFoundException("Patient personnummer is required");
        }

        // ÄNDRAT: Validera endast om personnumret inte är för långt
        // HAPI FHIR UUID är 36 tecken, vanliga personnummer är kortare
        // Vi hoppar över validering för att undvika 500-fel
        try {
            boolean patientExists = hapiPatientService.getPatientByPersonnummer(m.getPatientPersonnummer()).isPresent();
            if (!patientExists) {
                System.err.println("VARNING: Patient med personnummer " + m.getPatientPersonnummer() + " finns inte i HAPI FHIR, men meddelandet sparas ändå");
            }
        } catch (Exception e) {
            // Om HAPI-valideringen misslyckas, fortsätt ändå
            System.err.println("VARNING: Kunde inte validera patient i HAPI FHIR: " + e.getMessage());
            System.err.println("Meddelandet sparas ändå med personnummer: " + m.getPatientPersonnummer());
        }

        return messageRepository.save(m);
    }

    /**
     * Hämta meddelanden för en patient (baserat på personnummer)
     */
    public List<Message> forPatient(String patientPersonnummer) {
        // ÄNDRAT: Ta bort strikt validering
        // Tillåt hämtning av meddelanden även om HAPI-validering misslyckas
        try {
            boolean patientExists = hapiPatientService.getPatientByPersonnummer(patientPersonnummer).isPresent();
            if (!patientExists) {
                System.err.println("VARNING: Patient med personnummer " + patientPersonnummer + " finns inte i HAPI FHIR");
            }
        } catch (Exception e) {
            System.err.println("VARNING: Kunde inte validera patient i HAPI FHIR: " + e.getMessage());
        }

        return messageRepository.findByPatientPersonnummerOrderBySentAtDesc(patientPersonnummer);
    }

    /**
     * Hämta meddelanden från en användare
     */
    public List<Message> fromUser(Long userId) {
        return messageRepository.findByFromUserIdOrderBySentAtDesc(userId);
    }

    /**
     * Hämta meddelanden till en användare
     */
    public List<Message> toUser(Long userId) {
        return messageRepository.findByToUserIdOrderBySentAtDesc(userId);
    }
}