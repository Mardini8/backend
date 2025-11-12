package com.PatientSystem.PatientSystem.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service för att hämta Encounter-data från HAPI FHIR servern
 */
@Service
@RequiredArgsConstructor
public class HapiEncounterService {

    private final HapiClientService hapiClient;

    /**
     * Hämta alla encounters från HAPI
     */
    public List<Encounter> getAllEncounters() {
        IGenericClient client = hapiClient.getClient();

        Bundle bundle = client
                .search()
                .forResource(Encounter.class)
                .returnBundle(Bundle.class)
                .execute();

        return BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                .stream()
                .map(entry -> (Encounter) entry.getResource())
                .toList();
    }

    /**
     * Hämta encounters för en specifik patient
     */
    public List<Encounter> getEncountersForPatient(String patientId) {
        try {
            IGenericClient client = hapiClient.getClient();

            Bundle bundle = client
                    .search()
                    .forResource(Encounter.class)
                    .where(Encounter.PATIENT.hasId(patientId))
                    .returnBundle(Bundle.class)
                    .execute();

            return BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                    .stream()
                    .map(entry -> (Encounter) entry.getResource())
                    .toList();
        } catch (Exception e) {
            System.err.println("Kunde inte hämta encounters för patient: " + patientId);
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Hämta en specifik encounter
     */
    public Optional<Encounter> getEncounterById(String id) {
        try {
            IGenericClient client = hapiClient.getClient();

            Encounter encounter = client
                    .read()
                    .resource(Encounter.class)
                    .withId(id)
                    .execute();

            return Optional.of(encounter);
        } catch (Exception e) {
            System.err.println("Kunde inte hitta encounter med ID: " + id);
            return Optional.empty();
        }
    }
}