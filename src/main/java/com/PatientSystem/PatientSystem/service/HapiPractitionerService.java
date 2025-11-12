package com.PatientSystem.PatientSystem.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service för att hämta Practitioner-data från HAPI FHIR servern
 */
@Service
@RequiredArgsConstructor
public class HapiPractitionerService {

    private final HapiClientService hapiClient;

    /**
     * Hämta alla practitioners från HAPI
     */
    public List<Practitioner> getAllPractitioners() {
        IGenericClient client = hapiClient.getClient();

        Bundle bundle = client
                .search()
                .forResource(Practitioner.class)
                .returnBundle(Bundle.class)
                .execute();

        return BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                .stream()
                .map(entry -> (Practitioner) entry.getResource())
                .toList();
    }

    /**
     * Hämta en specifik practitioner
     */
    public Optional<Practitioner> getPractitionerById(String id) {
        try {
            IGenericClient client = hapiClient.getClient();

            Practitioner practitioner = client
                    .read()
                    .resource(Practitioner.class)
                    .withId(id)
                    .execute();

            return Optional.of(practitioner);
        } catch (Exception e) {
            System.err.println("Kunde inte hitta practitioner med ID: " + id);
            return Optional.empty();
        }
    }

    /**
     * Sök practitioner baserat på namn
     */
    public List<Practitioner> searchPractitionerByName(String name) {
        try {
            IGenericClient client = hapiClient.getClient();

            Bundle bundle = client
                    .search()
                    .forResource(Practitioner.class)
                    .where(Practitioner.NAME.matches().value(name))
                    .returnBundle(Bundle.class)
                    .execute();

            return BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                    .stream()
                    .map(entry -> (Practitioner) entry.getResource())
                    .toList();
        } catch (Exception e) {
            System.err.println("Kunde inte söka practitioner med namn: " + name);
            e.printStackTrace();
            return List.of();
        }
    }
}