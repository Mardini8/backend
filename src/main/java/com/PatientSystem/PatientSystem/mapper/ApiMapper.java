package com.PatientSystem.PatientSystem.mapper;

import com.PatientSystem.PatientSystem.dto.*;
import com.PatientSystem.PatientSystem.model.*;

public class ApiMapper {

    // ========== USER ==========
    public static UserDTO toDTO(User u) {
        if (u == null) return null;
        return new UserDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().name() : null,
                u.getForeignId()
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
        u.setForeignId(dto.foreignId());
        return u;
    }

    // ========== ORGANIZATION ==========
    public static OrganizationDTO toDTO(Organization o) {
        if (o == null) return null;
        return new OrganizationDTO(
                o.getId(),
                o.getName(),
                o.getLocationId()
        );
    }

    public static Organization toEntity(OrganizationDTO dto) {
        if (dto == null) return null;
        Organization o = new Organization();
        o.setId(dto.id());
        o.setName(dto.name());
        o.setLocationId(dto.locationId());
        return o;
    }

    // ========== LOCATION ==========
    public static LocationDTO toDTO(Location l) {
        if (l == null) return null;
        return new LocationDTO(
                l.getId(),
                l.getName()
        );
    }

    public static Location toEntity(LocationDTO dto) {
        if (dto == null) return null;
        Location l = new Location();
        l.setId(dto.id());
        l.setName(dto.name());
        return l;
    }

    // ========== MESSAGE ==========
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
}