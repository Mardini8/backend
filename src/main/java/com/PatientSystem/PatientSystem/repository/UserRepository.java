package com.PatientSystem.PatientSystem.repository;

import com.PatientSystem.PatientSystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
}