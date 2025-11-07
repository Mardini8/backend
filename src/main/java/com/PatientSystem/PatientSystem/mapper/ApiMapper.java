package com.PatientSystem.PatientSystem.mapper;

import com.PatientSystem.PatientSystem.dto.*;
import com.PatientSystem.PatientSystem.model.*;

public class ApiMapper {

    // --- USER ---
    public static UserDTO toDTO(User u) {
        if (u == null) return null;
        return new UserDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().name() : null
        );
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User u = new User();
        u.setId(dto.id());
        u.setUsername(dto.username());
        u.setEmail(dto.email());
        if (dto.role() != null) {
            u.setRole(Role.valueOf(dto.role()));
        }
        return u;
    }

    // --- PATIENT ---
    public static PatientDTO toDTO(Patient p) {
        if (p == null) return null;
        return new PatientDTO(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getSocialSecurityNumber(),
                p.getDateOfBirth()
        );
    }

    public static Patient toEntity(PatientDTO dto) {
        if (dto == null) return null;
        Patient p = new Patient();
        p.setId(dto.id());
        p.setFirstName(dto.firstName());
        p.setLastName(dto.lastName());
        p.setSocialSecurityNumber(dto.socialSecurityNumber());
        p.setDateOfBirth(dto.dateOfBirth());
        return p;
    }

    // --- OBSERVATION ---
    public static ObservationDTO toDTO(Observation o) {
        if (o == null) return null;
        return new ObservationDTO(
                o.getId(),
                o.getPatientId(),
                o.getPerformerId(),
                o.getEncounterId(),
                o.getCode(),
                o.getValueText(),
                o.getEffectiveDateTime()
        );
    }

    public static Observation toEntity(ObservationDTO dto) {
        if (dto == null) return null;
        Observation o = new Observation();
        o.setId(dto.id());
        o.setPatientId(dto.patientId());
        o.setPerformerId(dto.performerId());
        o.setEncounterId(dto.encounterId());
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
                c.getPatientId(),
                c.getPractitionerId(),
                c.getCode(),
                c.getDisplay(),
                c.getAssertedDate()
        );
    }

    public static Condition toEntity(ConditionDTO dto) {
        if (dto == null) return null;
        Condition c = new Condition();
        c.setId(dto.id());
        c.setPatientId(dto.patientId());
        c.setPractitionerId(dto.practitionerId());
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
                e.getPatientId(),
                e.getPractitionerId(),
                e.getLocationId(),
                e.getStartTime(),
                e.getEndTime()
        );
    }

    public static Encounter toEntity(EncounterDTO dto) {
        if (dto == null) return null;
        Encounter e = new Encounter();
        e.setId(dto.id());
        e.setPatientId(dto.patientId());
        e.setPractitionerId(dto.practitionerId());
        e.setLocationId(dto.locationId());
        e.setStartTime(dto.startTime());
        e.setEndTime(dto.endTime());
        return e;
    }

    // --- MESSAGE ---
    public static MessageDTO toDTO(Message m) {
        if (m == null) return null;
        return new MessageDTO(
                m.getId(),
                m.getFromUserId(),
                m.getToUserId(),
                m.getPatientId(),
                m.getContent(),
                m.getSentAt()
        );
    }

    public static Message toEntity(MessageDTO dto) {
        if (dto == null) return null;
        Message m = new Message();
        m.setId(dto.id());
        m.setFromUserId(dto.fromUserId());
        m.setToUserId(dto.toUserId());
        m.setPatientId(dto.patientId());
        m.setContent(dto.content());
        m.setSentAt(dto.sentAt());
        return m;
    }

    // --- PRACTITIONER ---
    public static PractitionerDTO toDTO(Practitioner p) {
        if (p == null) return null;
        return new PractitionerDTO(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getSocialSecurityNumber(),
                p.getDateOfBirth(),
                p.getOrganizationId()
        );
    }

    public static Practitioner toEntity(PractitionerDTO dto) {
        if (dto == null) return null;
        Practitioner p = new Practitioner();
        p.setId(dto.id());
        p.setFirstName(dto.firstName());
        p.setLastName(dto.lastName());
        p.setSocialSecurityNumber(dto.socialSecurityNumber());
        p.setDateOfBirth(dto.dateOfBirth());
        p.setOrganizationId(dto.organizationId());
        return p;
    }

    // --- LOCATION ---
    public static LocationDTO toDTO(Location l) {
        if (l == null) return null;
        return new LocationDTO(
                l.getId(),
                l.getName(),
                l.getOrganizationId()
        );
    }

    public static Location toEntity(LocationDTO dto) {
        if (dto == null) return null;
        Location l = new Location();
        l.setId(dto.id());
        l.setName(dto.name());
        l.setOrganizationId(dto.organizationId());
        return l;
    }

    // --- ORGANIZATION ---
    public static OrganizationDTO toDTO(Organization o) {
        if (o == null) return null;
        return new OrganizationDTO(
                o.getId(),
                o.getName()
        );
    }

    public static Organization toEntity(OrganizationDTO dto) {
        if (dto == null) return null;
        Organization o = new Organization();
        o.setId(dto.id());
        o.setName(dto.name());
        return o;
    }
}