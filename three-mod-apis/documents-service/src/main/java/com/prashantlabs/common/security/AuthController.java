package com.prashantlabs.common.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwt;

    public AuthController(JwtUtil j) {
        this.jwt = j;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String user) {
        return ResponseEntity.ok(Map.of("token", jwt.generate(user)));
    }
}
