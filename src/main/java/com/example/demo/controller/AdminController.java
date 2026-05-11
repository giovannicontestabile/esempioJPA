package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')") // 👈 Spring aggiunge automaticamente ROLE_
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("🔐 Benvenuto Admin! Accesso autorizzato.");
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> userDashboard() {
        return ResponseEntity.ok("👥 Lista utenti visibile a USER e ADMIN.");
    }
}