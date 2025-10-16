package com.example.monitoreo.monitoreo_gpon_back.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.monitoreo.monitoreo_gpon_back.security.JwtUtil;

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
        // para testing acepta cualquier username/password donde password == password
        if (username == null || password == null || !password.equals("password")) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
        
        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token, "tokenType", "Bearer", "expiresIn", jwtUtil.getExpirationSeconds()));
    }
}
