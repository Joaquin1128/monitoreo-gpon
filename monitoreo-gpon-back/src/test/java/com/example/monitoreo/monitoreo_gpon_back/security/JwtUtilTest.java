package com.example.monitoreo.monitoreo_gpon_back.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class JwtUtilTest {

    @Test
    public void generateAndValidateToken() throws Exception {
        JwtUtil util = new JwtUtil("my-secret-for-tests", 3600);
        String token = util.generateToken("user123");
        assertNotNull(token);
        assertTrue(util.validateToken(token));
        assertEquals("user123", util.getSubject(token));
    }

    @Test
    public void expiredTokenIsInvalid() throws Exception {
        JwtUtil util = new JwtUtil("another-secret", 1);
        String token = util.generateToken("u");
        assertTrue(util.validateToken(token));
        Thread.sleep(2000);
        assertFalse(util.validateToken(token));
    }
}
