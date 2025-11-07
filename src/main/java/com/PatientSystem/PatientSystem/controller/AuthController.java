package com.PatientSystem.PatientSystem.controller;

import com.PatientSystem.PatientSystem.dto.UserDTO;
import com.PatientSystem.PatientSystem.mapper.ApiMapper;
import com.PatientSystem.PatientSystem.model.Role;
import com.PatientSystem.PatientSystem.model.User;
import com.PatientSystem.PatientSystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    public record RegisterRequest(String username, String email, String password, Role role) {}
    public record LoginRequest(String username, String password) {}

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest req) {
        User u = auth.register(req.username(), req.email(), req.password(), req.role());
        return ResponseEntity.ok(ApiMapper.toDTO(u));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest req) {
        User u = auth.login(req.username(), req.password());
        if (u == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(ApiMapper.toDTO(u));
    }
}