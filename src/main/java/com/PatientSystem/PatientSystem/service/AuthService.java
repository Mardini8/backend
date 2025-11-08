package com.PatientSystem.PatientSystem.service;

import aj.org.objectweb.asm.commons.Remapper;
import com.PatientSystem.PatientSystem.model.Role;
import com.PatientSystem.PatientSystem.model.User;
import com.PatientSystem.PatientSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository users;
    public AuthService(UserRepository users){ this.users = users; }

    public User register(String username, String email, String password, Role role,Long foreignId){
        if(users.existsByUsername(username)) throw new IllegalArgumentException("Username taken");
        User u = new User();
        u.setUsername(username);
        u.setPassword(password); // OBS! för labb; i skarpt: bcrypt
        u.setRole(role);
        u.setForeignId(foreignId);
        return users.save(u);
    }

    public User login(String username, String password){
        User u = users.findByUsername(username);
        if(u == null || !u.getPassword().equals(password)) return null;
        return u; // i labb kan frontend “spara” användaren i state
    }

    /**
     * Hämta användare via ID
     */
    public Optional<User> getUserById(Long id) {
        return users.findById(id);
    }

    /**
     * Hämta användare via foreignId
     */
    public Optional<User> getUserByForeignId(Long foreignId) {
        return users.findByForeignId(foreignId);
    }
}
