package com.PatientSystem.PatientSystem.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service för att hämta Observation-data från HAPI FHIR servern
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
}