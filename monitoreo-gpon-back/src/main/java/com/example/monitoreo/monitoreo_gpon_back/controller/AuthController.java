package com.example.monitoreo.monitoreo_gpon_back.controller;

import com.example.monitoreo.monitoreo_gpon_back.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        // For demo: accept any username/password where password == "password".
        if (username == null || password == null || !password.equals("password")) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token, "tokenType", "Bearer", "expiresIn", jwtUtil.getExpirationSeconds()));
    }
}
