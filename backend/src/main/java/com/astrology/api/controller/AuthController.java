package com.astrology.api.controller;

import com.astrology.api.dto.AuthRequest;
import com.astrology.api.dto.AuthResponse;
import com.astrology.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            userService.registerUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse("User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            String token = userService.loginUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse("Login successful", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(new AuthResponse("Token is valid"));
        }
        return ResponseEntity.status(401).body(new AuthResponse("Invalid token"));
    }
} 