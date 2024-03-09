package com.localeconnect.app.user.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil = new JwtUtil();

    @Test
    void testTokenGenerationAndValidation() {
        jwtUtil.secret = "5367566B59703373367639792F423F5367566B59703373367639792F423F";
        String userName = "testuser";
        String token = jwtUtil.generateToken(userName);

        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
        assertEquals(userName, jwtUtil.extractEmail(token));
    }

}
