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
 * UPPDATERAD: Inkluderar alla obligatoriska FHIR-fält
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
     * UPPDATERAD: Med alla obligatoriska fält enligt FHIR R4 standard
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

        // Skapa observation enligt HAPI FHIR best practices
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);

        // Lägg till category
        observation.addCategory()
                .addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital signs");

        // Sätt code med LOINC
        observation.getCode()
                .addCoding()
                .setSystem("http://loinc.org")
                .setCode("8310-5")
                .setDisplay(description);
        observation.getCode().setText(description);

        // Sätt patient reference
        observation.setSubject(new Reference("Patient/" + patientPersonnummer));

        // Sätt performer (om angiven)
        if (performerPersonnummer != null && !performerPersonnummer.isEmpty()) {
            observation.addPerformer(new Reference("Practitioner/" + performerPersonnummer));
        }

        // Sätt värde om angivet
        if (value != null && !value.isEmpty()) {
            try {
                double numericValue = Double.parseDouble(value);
                Quantity quantity = new Quantity()
                        .setValue(numericValue)
                        .setUnit(unit != null && !unit.isEmpty() ? unit : "{score}")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode(unit != null && !unit.isEmpty() ? unit : "{score}");
                observation.setValue(quantity);
            } catch (NumberFormatException e) {
                // Om värdet inte är ett nummer, sätt som string
                observation.setValue(new StringType(value));
            }
        }

        // Sätt datum
        observation.setEffective(new DateTimeType(effectiveDateTime));
        observation.setIssued(effectiveDateTime);

        // Skapa i HAPI
        try {
            MethodOutcome outcome = client
                    .create()
                    .resource(observation)
                    .execute();

            // Hämta tillbaka den skapade resursen
            String newId = outcome.getId().getIdPart();
            System.out.println("Observation skapad med ID: " + newId);
            return getObservationById(newId).orElse(observation);
        } catch (Exception e) {
            System.err.println("Fel vid skapande av observation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}