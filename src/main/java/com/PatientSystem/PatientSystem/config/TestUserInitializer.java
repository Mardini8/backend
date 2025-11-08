package com.PatientSystem.PatientSystem.config;

import com.PatientSystem.PatientSystem.model.*;
import com.PatientSystem.PatientSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TestUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;

    @Override
    public void run(String... args) throws Exception {
        String defaultPassword = "password123";

        // --- 2. Skapa Practitioner för Läkare ---
        Practitioner doctor = createPractitionerIfNotExists(
                "doktor",
                "Lars",
                "Larsson",
                "19700101-1234",
                "1970-01-01",
                "Doctor"
        );

        // --- 3. Skapa Practitioner för Staff ---
        Practitioner staff = createPractitionerIfNotExists(
                "staff_person",
                "Maria",
                "Mariasson",
                "19800101-5678",
                "1980-01-01",
                "Staff"
        );

        // --- 4. Skapa Patient ---
        Patient patient = createPatientIfNotExists(
                "patient_user",
                "Anna",
                "Andersson",
                "19900101-9999",
                "1990-01-01"
        );

        // --- 5. Skapa User för Läkare (koppla till doctor practitioner) ---
        createUserIfNotExists("doktor", defaultPassword, Role.DOCTOR, doctor.getId());

        // --- 6. Skapa User för Staff (koppla till staff practitioner) ---
        createUserIfNotExists("staff", defaultPassword, Role.STAFF, staff.getId());

        // --- 7. Skapa User för Patient (koppla till patient) ---
        createUserIfNotExists("patient", defaultPassword, Role.PATIENT, patient.getId());

    }

    private Practitioner createPractitionerIfNotExists(
            String ssn, String firstName, String lastName,
            String socialSecurityNumber, String dateOfBirth, String title) {

        Optional<Practitioner> existing = practitionerRepository.findBySocialSecurityNumber(socialSecurityNumber);

        if (existing.isEmpty()) {
            Practitioner p = new Practitioner();
            p.setFirstName(firstName);
            p.setLastName(lastName);
            p.setSocialSecurityNumber(socialSecurityNumber);
            p.setDateOfBirth(dateOfBirth);
            p.setTitle(title);

            Practitioner saved = practitionerRepository.save(p);
            System.out.println("Skapade practitioner: " + firstName + " " + lastName + " (" + title + ")");
            return saved;
        }

        return existing.get();
    }

    private Patient createPatientIfNotExists(
            String ssn, String firstName, String lastName,
            String socialSecurityNumber, String dateOfBirth) {

        Optional<Patient> existing = patientRepository.findBySocialSecurityNumber(socialSecurityNumber);

        if (existing.isEmpty()) {
            Patient p = new Patient();
            p.setFirstName(firstName);
            p.setLastName(lastName);
            p.setSocialSecurityNumber(socialSecurityNumber);
            p.setDateOfBirth(dateOfBirth);

            Patient saved = patientRepository.save(p);
            System.out.println("Skapade patient: " + firstName + " " + lastName);
            return saved;
        }

        return existing.get();
    }

    private void createUserIfNotExists(String username, String password, Role role, Long foreignId) {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(username));

        if (existingUser.isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setForeignId(foreignId);

            userRepository.save(user);
            System.out.println("Skapade user: " + username + " (" + role.name() + ", foreignId=" + foreignId + ")");
        }
    }
}