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
 * Service för att hämta och skapa Encounter-data i HAPI FHIR servern
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

    /**
     * Skapa ett nytt encounter (besök) i HAPI FHIR
     *
     * @param patientPersonnummer Patient UUID från HAPI
     * @param practitionerPersonnummer Practitioner UUID från HAPI (valfri)
     * @param startTime Starttid för besöket
     * @param endTime Sluttid för besöket (valfri)
     * @return Det skapade encountern
     */
    public Encounter createEncounter(
            String patientPersonnummer,
            String practitionerPersonnummer,
            Date startTime,
            Date endTime
    ) {
        IGenericClient client = hapiClient.getClient();

        // Skapa encounter
        Encounter encounter = new Encounter();
        encounter.setStatus(Encounter.EncounterStatus.FINISHED);

        // Sätt patient
        encounter.setSubject(new Reference("Patient/" + patientPersonnummer));

        // Sätt practitioner som participant (om angiven)
        if (practitionerPersonnummer != null && !practitionerPersonnummer.isEmpty()) {
            Encounter.EncounterParticipantComponent participant = new Encounter.EncounterParticipantComponent();
            participant.setIndividual(new Reference("Practitioner/" + practitionerPersonnummer));
            encounter.addParticipant(participant);
        }

        // Sätt period (start och sluttid)
        Period period = new Period();
        period.setStart(startTime);
        if (endTime != null) {
            period.setEnd(endTime);
        }
        encounter.setPeriod(period);

        // Skapa i HAPI
        MethodOutcome outcome = client
                .create()
                .resource(encounter)
                .execute();

        // Hämta tillbaka den skapade resursen
        String newId = outcome.getId().getIdPart();
        return getEncounterById(newId).orElse(encounter);
    }
}