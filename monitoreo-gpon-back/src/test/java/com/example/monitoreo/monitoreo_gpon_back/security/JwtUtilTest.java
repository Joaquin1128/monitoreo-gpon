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

    @Test
    public void manipulatedTokenIsInvalid() throws Exception {
        JwtUtil util = new JwtUtil("secret-abc", 3600);
        String token = util.generateToken("userX");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
        String payload = parts[1];
        String tamperedPayload = payload.substring(0, payload.length()-1) + (payload.charAt(payload.length()-1) == 'a' ? 'b' : 'a');
        String tampered = parts[0] + "." + tamperedPayload + "." + parts[2];
        assertFalse(util.validateToken(tampered));
    }

    @Test
    public void nullOrEmptyTokenHandled() throws Exception {
        JwtUtil util = new JwtUtil("s", 3600);
        assertFalse(util.validateToken(null));
        assertFalse(util.validateToken(""));
    }

    @Test
    public void getSubjectThrowsForInvalidToken() throws Exception {
        JwtUtil util = new JwtUtil("secret-xyz", 3600);
        String token = util.generateToken("userY");
        String[] parts = token.split("\\.");
        String payload = parts[1];
        String tamperedPayload = payload.substring(0, payload.length()-1) + (payload.charAt(payload.length()-1) == 'a' ? 'b' : 'a');
        String tampered = parts[0] + "." + tamperedPayload + "." + parts[2];

        assertThrows(Exception.class, () -> util.getSubject(tampered));
    }

    @Test
    public void getExpirationSecondsReflectsConstructor() throws Exception {
        JwtUtil util = new JwtUtil("another-secret", 42);
        assertEquals(42L, util.getExpirationSeconds());
    }
}
