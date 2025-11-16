package com.PatientSystem.PatientSystem.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service för att hämta och skapa Condition-data i HAPI FHIR servern
 * UPPDATERAD: Inkluderar alla obligatoriska FHIR-fält
 */
@Service
@RequiredArgsConstructor
public class HapiConditionService {

    private final HapiClientService hapiClient;

    /**
     * Hämta alla conditions från HAPI
     */
    public List<Condition> getAllConditions() {
        IGenericClient client = hapiClient.getClient();

        Bundle bundle = client
                .search()
                .forResource(Condition.class)
                .returnBundle(Bundle.class)
                .execute();

        return BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                .stream()
                .map(entry -> (Condition) entry.getResource())
                .toList();
    }

    /**
     * Hämta conditions för en specifik patient
     */
    public List<Condition> getConditionsForPatient(String patientId) {
        try {
            IGenericClient client = hapiClient.getClient();

            Bundle bundle = client
                    .search()
                    .forResource(Condition.class)
                    .where(Condition.PATIENT.hasId(patientId))
                    .returnBundle(Bundle.class)
                    .execute();

            return BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                    .stream()
                    .map(entry -> (Condition) entry.getResource())
                    .toList();
        } catch (Exception e) {
            System.err.println("Kunde inte hämta conditions för patient: " + patientId);
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Hämta en specifik condition
     */
    public Optional<Condition> getConditionById(String id) {
        try {
            IGenericClient client = hapiClient.getClient();

            Condition condition = client
                    .read()
                    .resource(Condition.class)
                    .withId(id)
                    .execute();

            return Optional.of(condition);
        } catch (Exception e) {
            System.err.println("Kunde inte hitta condition med ID: " + id);
            return Optional.empty();
        }
    }

    /**
     * Skapa en ny condition (diagnos) i HAPI FHIR
     * UPPDATERAD: Med alla obligatoriska fält enligt FHIR R4 standard
     *
     * @param patientPersonnummer Patient UUID från HAPI
     * @param practitionerPersonnummer Practitioner UUID från HAPI (valfri)
     * @param description Beskrivning av diagnosen
     * @param recordedDate Datum när diagnosen registrerades
     * @return Den skapade conditionen
     */
    public Condition createCondition(
            String patientPersonnummer,
            String practitionerPersonnummer,
            String description,
            Date recordedDate
    ) {
        IGenericClient client = hapiClient.getClient();

        // Skapa condition enligt HAPI FHIR best practices
        Condition condition = new Condition();

        // Sätt clinicalStatus (obligatoriskt)
        condition.getClinicalStatus()
                .addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/condition-clinical")
                .setCode("active");

        // Sätt verificationStatus (obligatoriskt)
        condition.getVerificationStatus()
                .addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/condition-ver-status")
                .setCode("confirmed");

        // Sätt patient reference
        condition.setSubject(new Reference("Patient/" + patientPersonnummer));

        // Sätt recorder (om angiven)
        if (practitionerPersonnummer != null && !practitionerPersonnummer.isEmpty()) {
            condition.setRecorder(new Reference("Practitioner/" + practitionerPersonnummer));
        }

        // Sätt code med SNOMED
        condition.getCode()
                .addCoding()
                .setSystem("http://snomed.info/sct")
                .setCode("404684003")
                .setDisplay(description);
        condition.getCode().setText(description);

        // Sätt datum
        condition.setRecordedDate(recordedDate);
        condition.setOnset(new DateTimeType(recordedDate));

        // Skapa i HAPI
        try {
            MethodOutcome outcome = client
                    .create()
                    .resource(condition)
                    .execute();

            // Hämta tillbaka den skapade resursen
            String newId = outcome.getId().getIdPart();
            System.out.println("Condition skapad med ID: " + newId);
            return getConditionById(newId).orElse(condition);
        } catch (Exception e) {
            System.err.println("Fel vid skapande av condition: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}