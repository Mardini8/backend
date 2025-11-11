package com.PatientSystem.PatientSystem.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.springframework.stereotype.Service;

/**
 * Service för att kommunicera med HAPI FHIR servern
 * Används för att hämta Patient, Observation, Condition, Encounter, och Practitioner
 */
@Service
public class HapiClientService {

    private final IGenericClient client;
    private final FhirContext context;

    public HapiClientService() {
        // Skapa FHIR context för R4 (Release 4)
        this.context = FhirContext.forR4();

        // Bas-URL för HAPI FHIR servern
        String baseURL = "https://hapi-fhir.app.cloud.cbh.kth.se/fhir";

        // Skapa klient
        this.client = context.newRestfulGenericClient(baseURL);

        System.out.println("HAPI FHIR Client initialiserad med URL: " + baseURL);
    }

    /**
     * Returnerar FHIR klienten för att göra anrop
     */
    public IGenericClient getClient() {
        return client;
    }

    /**
     * Returnerar FHIR context (behövs för vissa operationer)
     */
    public FhirContext getContext() {
        return context;
    }
}