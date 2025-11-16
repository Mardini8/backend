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
 * Service för att hämta och skapa Observation-data i HAPI FHIR servern
 */
@Service
@RequiredArgsConstructor
public class HapiObservationService {

    private final HapiClientService hapiClient;

    /**
     * Hämta alla observations från HAPI
     */
    public List<Observation> getAllObservations() {
        IGenericClient client = hapiClient.getClient();

        Bundle bundle = client
                .search()
                .forResource(Observation.class)
                .returnBundle(Bundle.class)
                .execute();

        return BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                .stream()
                .map(entry -> (Observation) entry.getResource())
                .toList();
    }

    /**
     * Hämta observations för en specifik patient
     */
    public List<Observation> getObservationsForPatient(String patientId) {
        try {
            IGenericClient client = hapiClient.getClient();

            Bundle bundle = client
                    .search()
                    .forResource(Observation.class)
                    .where(Observation.PATIENT.hasId(patientId))
                    .returnBundle(Bundle.class)
                    .execute();

            return BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                    .stream()
                    .map(entry -> (Observation) entry.getResource())
                    .toList();
        } catch (Exception e) {
            System.err.println("Kunde inte hämta observations för patient: " + patientId);
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Hämta en specifik observation
     */
    public Optional<Observation> getObservationById(String id) {
        try {
            IGenericClient client = hapiClient.getClient();

            Observation observation = client
                    .read()
                    .resource(Observation.class)
                    .withId(id)
                    .execute();

            return Optional.of(observation);
        } catch (Exception e) {
            System.err.println("Kunde inte hitta observation med ID: " + id);
            return Optional.empty();
        }
    }

    /**
     * Skapa en ny observation i HAPI FHIR
     *
     * @param patientPersonnummer Patient UUID från HAPI
     * @param performerPersonnummer Practitioner UUID från HAPI (valfri)
     * @param description Beskrivning av observationen
     * @param value Värde (valfri)
     * @param unit Enhet (valfri)
     * @param effectiveDateTime Datum och tid för observationen
     * @return Den skapade observationen
     */
    public Observation createObservation(
            String patientPersonnummer,
            String performerPersonnummer,
            String description,
            String value,
            String unit,
            Date effectiveDateTime
    ) {
        IGenericClient client = hapiClient.getClient();

        // Skapa observation
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);

        // Sätt patient
        observation.setSubject(new Reference("Patient/" + patientPersonnummer));

        // Sätt performer (om angiven)
        if (performerPersonnummer != null && !performerPersonnummer.isEmpty()) {
            observation.addPerformer(new Reference("Practitioner/" + performerPersonnummer));
        }

        // Sätt beskrivning (code)
        CodeableConcept code = new CodeableConcept();
        code.setText(description);
        observation.setCode(code);

        // Sätt värde om angivet
        if (value != null && !value.isEmpty()) {
            Quantity quantity = new Quantity();
            try {
                quantity.setValue(Double.parseDouble(value));
                if (unit != null && !unit.isEmpty()) {
                    quantity.setUnit(unit);
                }
                observation.setValue(quantity);
            } catch (NumberFormatException e) {
                // Om värdet inte är ett nummer, sätt som string
                observation.setValue(new StringType(value));
            }
        }

        // Sätt datum
        observation.setEffective(new DateTimeType(effectiveDateTime));

        // Skapa i HAPI
        MethodOutcome outcome = client
                .create()
                .resource(observation)
                .execute();

        // Hämta tillbaka den skapade resursen
        String newId = outcome.getId().getIdPart();
        return getObservationById(newId).orElse(observation);
    }
}