package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.model.Condition;
import com.PatientSystem.PatientSystem.model.Encounter;
import com.PatientSystem.PatientSystem.model.Observation;
import com.PatientSystem.PatientSystem.service.ObservationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clinical")
public class ObservationController {
    private final ObservationService service;
    public ObservationController(ObservationService service){ this.service = service; }

    @GetMapping("/patients/{id}/observations")
    public List<Observation> observations(@PathVariable Long id){
        return service.observationsForPatient(id);
    }
    @PostMapping("/observations")
    public Observation createObservation(@RequestBody Observation o){
        return service.saveObservation(o);
    }

    @GetMapping("/patients/{id}/conditions")
    public List<Condition> conditions(@PathVariable Long id){
        return service.conditionsForPatient(id);
    }
    @PostMapping("/conditions")
    public Condition createCondition(@RequestBody Condition c){
        return service.saveCondition(c);
    }

    @GetMapping("/patients/{id}/encounters")
    public List<Encounter> encounters(@PathVariable Long id){
        return service.encountersForPatient(id);
    }
    @PostMapping("/encounters")
    public Encounter createEncounter(@RequestBody Encounter e){
        return service.saveEncounter(e);
    }
}
