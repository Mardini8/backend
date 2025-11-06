package com.PatientSystem.PatientSystem.service;

import com.PatientSystem.PatientSystem.model.*;
import com.PatientSystem.PatientSystem.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ObservationService {
    private final ObservationRepository obsRepository;
    private final ConditionRepository condRepository;
    private final EncounterRepository encRepository;

    public ObservationService(ObservationRepository o, ConditionRepository c, EncounterRepository e) {
        this.obsRepository = o; this.condRepository = c; this.encRepository = e;
    }

    public List<Observation> observationsForPatient(Long patientId){ return obsRepository.findByPatientId(patientId); }
    public Observation saveObservation(Observation o){ return obsRepository.save(o); }

    public List<Condition> conditionsForPatient(Long patientId){ return condRepository.findByPatientId(patientId); }
    public Condition saveCondition(Condition c){ return condRepository.save(c); }

    public List<Encounter> encountersForPatient(Long patientId){ return encRepository.findByPatientId(patientId); }
    public Encounter saveEncounter(Encounter e){ return encRepository.save(e); }
}
