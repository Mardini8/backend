package com.PatientSystem.PatientSystem.mapper;

import com.PatientSystem.PatientSystem.dto.PatientDTO;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

import java.time.ZoneId;
import java.util.Date;

/**
 * Mapper för att konvertera FHIR Patient till vårt PatientDTO
 */
public class PatientMapper {

    /**
     * Konvertera FHIR Patient till PatientDTO
     */
    public static PatientDTO toDTO(Patient fhirPatient) {
        if (fhirPatient == null) {
            return null;
        }

        // Hämta ID från FHIR Patient
        Long id = extractNumericId(fhirPatient.getIdElement().getIdPart());

        // Hämta namn (första namnet i listan)
        String firstName = "";
        String lastName = "";
        if (!fhirPatient.getName().isEmpty()) {
            HumanName name = fhirPatient.getName().get(0);
            if (!name.getGiven().isEmpty()) {
                firstName = name.getGiven().get(0).getValue();
            }
            lastName = name.getFamily();
        }

        // Hämta personnummer från Identifier
        String socialSecurityNumber = extractPersonnummer(fhirPatient);

        // Hämta födelsedatum
        Date birthDate = fhirPatient.getBirthDate();
        String dateOfBirth = null;
        if (birthDate != null) {
            dateOfBirth = birthDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString();
        }

        return new PatientDTO(
                id,
                firstName,
                lastName,
                socialSecurityNumber,
                dateOfBirth
        );
    }

    /**
     * Extrahera personnummer från FHIR Patient Identifiers
     * Letar efter identifier med system som innehåller "personnummer" eller "ssn"
     */
    private static String extractPersonnummer(Patient patient) {
        for (Identifier identifier : patient.getIdentifier()) {
            // Kolla om det är ett personnummer
            if (identifier.hasSystem()) {
                String system = identifier.getSystem().toLowerCase();
                if (system.contains("personnummer") || system.contains("ssn")) {
                    return identifier.getValue();
                }
            }
            // Om inget system, ta första identifiern
            if (identifier.hasValue()) {
                return identifier.getValue();
            }
        }
        return "Okänt";
    }

    /**
     * Konvertera FHIR ID (som kan vara "Patient/123") till numeriskt ID
     */
    private static Long extractNumericId(String fhirId) {
        try {
            // Om ID:t är "Patient/123", ta bort "Patient/"
            if (fhirId.contains("/")) {
                fhirId = fhirId.substring(fhirId.lastIndexOf("/") + 1);
            }
            return Long.parseLong(fhirId);
        } catch (NumberFormatException e) {
            // Om ID inte är numeriskt, returnera 0
            System.err.println("Kunde inte konvertera FHIR ID till nummer: " + fhirId);
            return 0L;
        }
    }
}