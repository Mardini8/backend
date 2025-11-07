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
        // Inaktivera CSRF (Cross-Site Request Forgery)
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Använd "/**" för att matcha ALLA sökvägar rekursivt
                        // 2. Ta bort ".anyRequest().authenticated()" helt
                        .requestMatchers("/**").permitAll()
                );

        return http.build();
    }
}