package com.example.monitoreo.monitoreo_gpon_back.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

public class JwtAuthenticationFilterTest {

    @AfterEach
    public void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void validToken_setsAuthentication() throws ServletException, IOException {
        JwtUtil util = mock(JwtUtil.class);
        when(util.validateToken("tok" )).thenReturn(true);
        when(util.getSubject("tok")).thenReturn("user1");

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(util);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer tok");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = (request, response) -> { };

        filter.doFilter(req, resp, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("user1", auth.getPrincipal());
    }

    @Test
    public void invalidToken_noAuthenticationSet() throws ServletException, IOException {
        JwtUtil util = mock(JwtUtil.class);
        when(util.validateToken("bad" )).thenReturn(false);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(util);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer bad");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = (request, response) -> { };

        filter.doFilter(req, resp, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    public void exceptionFromUtil_isHandled() throws ServletException, IOException {
        JwtUtil util = mock(JwtUtil.class);
        when(util.validateToken("err" )).thenThrow(new RuntimeException("boom"));

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(util);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer err");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = (request, response) -> { };

        filter.doFilter(req, resp, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
