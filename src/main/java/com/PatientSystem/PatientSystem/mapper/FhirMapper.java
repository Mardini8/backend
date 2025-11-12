package com.PatientSystem.PatientSystem.mapper;

import com.PatientSystem.PatientSystem.dto.*;
import org.hl7.fhir.r4.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Mapper för FHIR-resurser från HAPI
 * Separat från ApiMapper för att undvika konflikter med lokala entiteter
 */
public class FhirMapper {

    // ==================== PATIENT ====================

    /**
     * Konvertera FHIR Patient till PatientDTO
     */
    public static PatientDTO patientToDTO(org.hl7.fhir.r4.model.Patient fhirPatient) {
        if (fhirPatient == null) {
            return null;
        }

        // VIKTIGT: Använd hela FHIR ID (kan vara UUID)
        // T.ex. "0ecb18bc-4261-2ab0-9c46-336617b839be"
        String fhirId = fhirPatient.getIdElement().getIdPart();

        // Försök konvertera till Long, men behåll String om det misslyckas
        Long id = extractNumericId(fhirId);

        String firstName = "";
        String lastName = "";
        if (!fhirPatient.getName().isEmpty()) {
            HumanName name = fhirPatient.getName().get(0);
            if (!name.getGiven().isEmpty()) {
                firstName = name.getGiven().get(0).getValue();
            }
            if (name.hasFamily()) {
                lastName = name.getFamily();
            }
        }

        String socialSecurityNumber = extractIdentifier(fhirPatient.getIdentifier());

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

    // ==================== CONDITION ====================

    /**
     * Konvertera FHIR Condition till ConditionDTO
     */
    public static ConditionDTO conditionToDTO(org.hl7.fhir.r4.model.Condition fhirCondition) {
        if (fhirCondition == null) {
            return null;
        }

        Long id = extractNumericId(fhirCondition.getIdElement().getIdPart());

        // Hämta patient ID från subject
        Long patientId = null;
        if (fhirCondition.hasSubject()) {
            patientId = extractNumericId(fhirCondition.getSubject().getReferenceElement().getIdPart());
        }

        // Hämta practitioner ID från asserter/recorder
        Long practitionerId = null;
        if (fhirCondition.hasAsserter()) {
            practitionerId = extractNumericId(fhirCondition.getAsserter().getReferenceElement().getIdPart());
        } else if (fhirCondition.hasRecorder()) {
            practitionerId = extractNumericId(fhirCondition.getRecorder().getReferenceElement().getIdPart());
        }

        // Hämta beskrivning från code
        String description = "Okänd diagnos";
        if (fhirCondition.hasCode() && fhirCondition.getCode().hasText()) {
            description = fhirCondition.getCode().getText();
        } else if (fhirCondition.hasCode() && !fhirCondition.getCode().getCoding().isEmpty()) {
            description = fhirCondition.getCode().getCoding().get(0).getDisplay();
        }

        // Hämta datum - konvertera till LocalDate
        java.time.LocalDate assertedDate = null;
        if (fhirCondition.hasRecordedDate()) {
            assertedDate = fhirCondition.getRecordedDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        return new ConditionDTO(
                id,
                patientId,
                practitionerId,
                description,
                assertedDate
        );
    }

    // ==================== OBSERVATION ====================

    /**
     * Konvertera FHIR Observation till ObservationDTO
     */
    public static ObservationDTO observationToDTO(org.hl7.fhir.r4.model.Observation fhirObservation) {
        if (fhirObservation == null) {
            return null;
        }

        Long id = extractNumericId(fhirObservation.getIdElement().getIdPart());

        // Hämta patient ID
        Long patientId = null;
        if (fhirObservation.hasSubject()) {
            patientId = extractNumericId(fhirObservation.getSubject().getReferenceElement().getIdPart());
        }

        // Hämta practitioner ID från performer
        Long practitionerId = null;
        if (!fhirObservation.getPerformer().isEmpty()) {
            Reference performer = fhirObservation.getPerformer().get(0);
            if (performer.getReferenceElement().getResourceType().equals("Practitioner")) {
                practitionerId = extractNumericId(performer.getReferenceElement().getIdPart());
            }
        }

        // Hämta encounter ID (om det finns)
        Long encounterId = null;
        if (fhirObservation.hasEncounter()) {
            encounterId = extractNumericId(fhirObservation.getEncounter().getReferenceElement().getIdPart());
        }

        // Hämta beskrivning från code
        String description = "Okänd observation";
        if (fhirObservation.hasCode() && fhirObservation.getCode().hasText()) {
            description = fhirObservation.getCode().getText();
        } else if (fhirObservation.hasCode() && !fhirObservation.getCode().getCoding().isEmpty()) {
            description = fhirObservation.getCode().getCoding().get(0).getDisplay();
        }

        // Lägg till värde i beskrivningen om det finns
        if (fhirObservation.hasValueQuantity()) {
            Quantity value = fhirObservation.getValueQuantity();
            description += ": " + value.getValue() + " " + value.getUnit();
        } else if (fhirObservation.hasValueStringType()) {
            description += ": " + fhirObservation.getValueStringType().getValue();
        }

        // Hämta datum - konvertera till LocalDateTime
        java.time.LocalDateTime effectiveDateTime = null;
        if (fhirObservation.hasEffectiveDateTimeType()) {
            effectiveDateTime = fhirObservation.getEffectiveDateTimeType().getValue().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        return new ObservationDTO(
                id,
                patientId,
                practitionerId,
                encounterId,
                description,
                effectiveDateTime
        );
    }

    // ==================== ENCOUNTER ====================

    /**
     * Konvertera FHIR Encounter till EncounterDTO
     */
    public static EncounterDTO encounterToDTO(org.hl7.fhir.r4.model.Encounter fhirEncounter) {
        if (fhirEncounter == null) {
            return null;
        }

        Long id = extractNumericId(fhirEncounter.getIdElement().getIdPart());

        // Hämta patient ID
        Long patientId = null;
        if (fhirEncounter.hasSubject()) {
            patientId = extractNumericId(fhirEncounter.getSubject().getReferenceElement().getIdPart());
        }

        // Hämta practitioner ID från participant
        Long practitionerId = null;
        if (!fhirEncounter.getParticipant().isEmpty()) {
            for (Encounter.EncounterParticipantComponent participant : fhirEncounter.getParticipant()) {
                if (participant.hasIndividual() &&
                        participant.getIndividual().getReferenceElement().getResourceType().equals("Practitioner")) {
                    practitionerId = extractNumericId(participant.getIndividual().getReferenceElement().getIdPart());
                    break;
                }
            }
        }

        // Hämta organization ID (om det finns)
        Long organizationId = null;
        if (fhirEncounter.hasServiceProvider()) {
            organizationId = extractNumericId(fhirEncounter.getServiceProvider().getReferenceElement().getIdPart());
        }

        // Hämta start- och sluttid - konvertera till LocalDateTime
        java.time.LocalDateTime startTime = null;
        java.time.LocalDateTime endTime = null;
        if (fhirEncounter.hasPeriod()) {
            Period period = fhirEncounter.getPeriod();
            if (period.hasStart()) {
                startTime = period.getStart().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
            if (period.hasEnd()) {
                endTime = period.getEnd().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        }

        return new EncounterDTO(
                id,
                patientId,
                practitionerId,
                organizationId,
                startTime,
                endTime
        );
    }

    // ==================== PRACTITIONER ====================

    /**
     * Konvertera FHIR Practitioner till PractitionerDTO
     */
    public static PractitionerDTO practitionerToDTO(org.hl7.fhir.r4.model.Practitioner fhirPractitioner) {
        if (fhirPractitioner == null) {
            return null;
        }

        Long id = extractNumericId(fhirPractitioner.getIdElement().getIdPart());

        String firstName = "";
        String lastName = "";
        if (!fhirPractitioner.getName().isEmpty()) {
            HumanName name = fhirPractitioner.getName().get(0);
            if (!name.getGiven().isEmpty()) {
                firstName = name.getGiven().get(0).getValue();
            }
            if (name.hasFamily()) {
                lastName = name.getFamily();
            }
        }

        String socialSecurityNumber = extractIdentifier(fhirPractitioner.getIdentifier());

        Date birthDate = fhirPractitioner.getBirthDate();
        String dateOfBirth = null;
        if (birthDate != null) {
            dateOfBirth = birthDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString();
        }

        // Hämta titel/roll
        String title = "Vårdpersonal";
        if (!fhirPractitioner.getQualification().isEmpty()) {
            Practitioner.PractitionerQualificationComponent qual = fhirPractitioner.getQualification().get(0);
            if (qual.hasCode() && qual.getCode().hasText()) {
                title = qual.getCode().getText();
            }
        }

        return new PractitionerDTO(
                id,
                firstName,
                lastName,
                socialSecurityNumber,
                dateOfBirth,
                title,
                null
        );
    }

    // ==================== HJÄLPMETODER ====================

    /**
     * Extrahera identifier från lista
     */
    private static String extractIdentifier(java.util.List<Identifier> identifiers) {
        for (Identifier identifier : identifiers) {
            if (identifier.hasValue()) {
                return identifier.getValue();
            }
        }
        return "Okänt";
    }

    /**
     * Konvertera FHIR ID till numeriskt ID
     */
    private static Long extractNumericId(String fhirId) {
        try {
            if (fhirId == null) return 0L;
            if (fhirId.contains("/")) {
                fhirId = fhirId.substring(fhirId.lastIndexOf("/") + 1);
            }
            return Long.parseLong(fhirId);
        } catch (NumberFormatException e) {
            System.err.println("Kunde inte konvertera FHIR ID: " + fhirId);
            return 0L;
        }
    }
}