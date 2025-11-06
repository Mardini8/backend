package com.PatientSystem.PatientSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Inaktivera CSRF och CORS är kvar som vi tidigare bestämde
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // VIKTIGT: Tillåt alla metoder (GET, POST, etc.) på /api/patients
                        .requestMatchers("/api/hello", "/api/patients/**").permitAll()

                        // Alla andra förfrågningar kräver autentisering (standard)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}