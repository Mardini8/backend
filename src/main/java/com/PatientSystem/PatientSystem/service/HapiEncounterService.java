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
 * UPPDATERAD: Inkluderar alla obligatoriska FHIR-fält
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
     * UPPDATERAD: Med alla obligatoriska fält enligt FHIR R4 standard
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

        // Skapa encounter enligt HAPI FHIR best practices
        Encounter encounter = new Encounter();
        encounter.setStatus(Encounter.EncounterStatus.FINISHED);

        // Sätt class (obligatoriskt)
        encounter.setClass_(new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                .setCode("AMB")
                .setDisplay("ambulatory"));

        // Lägg till type
        encounter.addType()
                .addCoding()
                .setSystem("http://snomed.info/sct")
                .setCode("185349003")
                .setDisplay("Encounter for check up (procedure)");

        // Sätt patient reference
        encounter.setSubject(new Reference("Patient/" + patientPersonnummer));

        // Sätt practitioner som participant (om angiven)
        if (practitionerPersonnummer != null && !practitionerPersonnummer.isEmpty()) {
            Encounter.EncounterParticipantComponent participant = encounter.addParticipant();

            // Lägg till participant type
            participant.addType()
                    .addCoding()
                    .setSystem("http://terminology.hl7.org/CodeSystem/v3-ParticipationType")
                    .setCode("PPRF")
                    .setDisplay("primary performer");

            // Sätt practitioner reference
            participant.setIndividual(new Reference("Practitioner/" + practitionerPersonnummer));

            // Sätt period för participant
            Period participantPeriod = new Period();
            participantPeriod.setStart(startTime);
            if (endTime != null) {
                participantPeriod.setEnd(endTime);
            }
            participant.setPeriod(participantPeriod);
        }

        // Sätt period (obligatoriskt)
        Period period = new Period();
        period.setStart(startTime);
        if (endTime != null) {
            period.setEnd(endTime);
        }
        encounter.setPeriod(period);

        // Skapa i HAPI
        try {
            MethodOutcome outcome = client
                    .create()
                    .resource(encounter)
                    .execute();

            // Hämta tillbaka den skapade resursen
            String newId = outcome.getId().getIdPart();
            System.out.println("Encounter skapat med ID: " + newId);
            return getEncounterById(newId).orElse(encounter);
        } catch (Exception e) {
            System.err.println("Fel vid skapande av encounter: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}