package com.becajava.userservice.adapter.out.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptPasswordEncoderAdapterTest {

    private PasswordEncoder passwordEncoder;
    private BCryptPasswordEncoderAdapter adapter;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        adapter = new BCryptPasswordEncoderAdapter(
                passwordEncoder
        );
    }

    @Test
    void shouldGenerateHashThatMatchesOriginalPassword() {
        String rawPassword = "  Senha123";

        String passwordHash = adapter.encode(rawPassword);

        assertNotEquals(rawPassword, passwordHash);

        assertTrue(
                passwordEncoder.matches(
                        rawPassword,
                        passwordHash
                )
        );

        assertFalse(
                passwordEncoder.matches(
                        rawPassword.trim(),
                        passwordHash
                )
        );
    }
}