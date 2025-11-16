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

        // Skapa condition
        Condition condition = new Condition();

        // Sätt patient
        condition.setSubject(new Reference("Patient/" + patientPersonnummer));

        // Sätt recorder/asserter (om angiven)
        if (practitionerPersonnummer != null && !practitionerPersonnummer.isEmpty()) {
            condition.setRecorder(new Reference("Practitioner/" + practitionerPersonnummer));
        }

        // Sätt beskrivning (code)
        CodeableConcept code = new CodeableConcept();
        code.setText(description);
        condition.setCode(code);

        // Sätt datum
        condition.setRecordedDate(recordedDate);

        // Skapa i HAPI
        MethodOutcome outcome = client
                .create()
                .resource(condition)
                .execute();

        // Hämta tillbaka den skapade resursen
        String newId = outcome.getId().getIdPart();
        return getConditionById(newId).orElse(condition);
    }
}