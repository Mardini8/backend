package com.PatientSystem.PatientSystem.mapper;

import com.PatientSystem.PatientSystem.dto.*;
import com.PatientSystem.PatientSystem.model.*;

public class ApiMapper {

    // --- USER ---
    public static UserDTO toDTO(User u) {
        if (u == null) return null;
        return new UserDTO(u.getId(), u.getUsername(), u.getEmail(),
                u.getRole() != null ? u.getRole().name() : null);
    }

    // --- PATIENT ---
    public static PatientDTO toDTO(Patient p) {
        if (p == null) return null;
        return new PatientDTO(p.getId(), p.getFirstName(), p.getLastName(), p.getDateOfBirth());
    }

    public static Patient toEntity(PatientDTO dto) {
        if (dto == null) return null;
        Patient p = new Patient();
        p.setFirstName(dto.givenName());
        p.setLastName(dto.familyName());
        p.setDateOfBirth(dto.birthDate());
        return p;
    }

    // --- OBSERVATION ---
    public static ObservationDTO toDTO(Observation o) {
        if (o == null) return null;
        return new ObservationDTO(
                o.getId(),
                o.getPatient() != null ? o.getPatient().getId() : null,
                o.getPerformer() != null ? o.getPerformer().getId() : null,
                o.getEncounter() != null ? o.getEncounter().getId() : null,
                o.getCode(),
                o.getValueText(),
                o.getEffectiveDateTime()
        );
    }

    public static Observation toEntity(ObservationDTO dto, Patient patient,
                                       Practitioner performer, Encounter encounter) {
        Observation o = new Observation();
        o.setPatient(patient);
        o.setPerformer(performer);
        o.setEncounter(encounter);
        o.setCode(dto.code());
        o.setValueText(dto.valueText());
        o.setEffectiveDateTime(dto.effectiveDateTime());
        return o;
    }

    // --- CONDITION ---
    public static ConditionDTO toDTO(Condition c) {
        if (c == null) return null;
        return new ConditionDTO(
                c.getId(),
                c.getPatient() != null ? c.getPatient().getId() : null,
                c.getRecorder() != null ? c.getRecorder().getId() : null,
                c.getCode(),
                c.getDisplay(),
                c.getAssertedDate()
        );
    }

    public static Condition toEntity(ConditionDTO dto, Patient patient, Practitioner recorder) {
        Condition c = new Condition();
        c.setPatient(patient);
        c.setRecorder(recorder);
        c.setCode(dto.code());
        c.setDisplay(dto.display());
        c.setAssertedDate(dto.assertedDate());
        return c;
    }

    // --- ENCOUNTER ---
    public static EncounterDTO toDTO(Encounter e) {
        if (e == null) return null;
        return new EncounterDTO(
                e.getId(),
                e.getPatient() != null ? e.getPatient().getId() : null,
                e.getPractitioner() != null ? e.getPractitioner().getId() : null,
                e.getLocation() != null ? e.getLocation().getId() : null,
                e.getStartTime(),
                e.getEndTime()
        );
    }

    public static Encounter toEntity(EncounterDTO dto, Patient p, Practitioner pr, Location l) {
        Encounter e = new Encounter();
        e.setPatient(p);
        e.setPractitioner(pr);
        e.setLocation(l);
        e.setStartTime(dto.startTime());
        e.setEndTime(dto.endTime());
        return e;
    }

    // --- MESSAGE ---
    public static MessageDTO toDTO(Message m) {
        if (m == null) return null;
        return new MessageDTO(
                m.getId(),
                m.getFromUser() != null ? m.getFromUser().getId() : null,
                m.getToUser() != null ? m.getToUser().getId() : null,
                m.getPatient() != null ? m.getPatient().getId() : null,
                m.getContent(),
                m.getSentAt()
        );
    }
}
