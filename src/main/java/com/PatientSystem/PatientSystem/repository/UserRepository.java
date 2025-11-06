package com.PatientSystem.PatientSystem.repository;

import com.PatientSystem.PatientSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
