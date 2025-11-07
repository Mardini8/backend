package com.PatientSystem.PatientSystem.config;

import com.PatientSystem.PatientSystem.model.Role;
import com.PatientSystem.PatientSystem.model.User;
import com.PatientSystem.PatientSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Denna komponent körs automatiskt när applikationen har startat
@Component
@RequiredArgsConstructor
public class TestUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Enkelt lösenord för testning, kommer att hash-as
        String defaultPassword = "password123";

        // --- 1. Skapa Läkare ---
        createUserIfNotExists("doktor", defaultPassword, Role.DOCTOR);

        // --- 2. Skapa Övrig Personal ---
        createUserIfNotExists("staff", defaultPassword, Role.STAFF);

        // --- 3. Skapa Patient ---
        createUserIfNotExists("patient", defaultPassword, Role.PATIENT);

        System.out.println("Testanvändare initierade. Lösenord för alla: " + defaultPassword);
    }

    // Hjälpmetod för att undvika dubletter
    private void createUserIfNotExists(String username, String password, Role role) {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(username));

        if (existingUser.isEmpty()) {
            User user = new User();
            user.setUsername(username);

            // VIKTIGT: Lösenordet måste hash-as innan det sparas i databasen
            user.setPassword(password);
            user.setRole(role);

            // OBS! Vi ignorerar Patient/Practitioner kopplingen här för testning,
            // men i full implementation skulle dessa också skapas och kopplas.

            userRepository.save(user);
            System.out.println("Skapade testanvändare: " + username + " (" + role.name() + ")");
        }
    }
}