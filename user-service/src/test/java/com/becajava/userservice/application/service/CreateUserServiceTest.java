package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.exception.EmailAlreadyExistsException;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.out.PasswordEncoderPort;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CreateUserServiceTest {

    private UserRepositoryPort userRepositoryPort;
    private PasswordEncoderPort passwordEncoderPort;
    private CreateUserService createUserService;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);

        createUserService = new CreateUserService(
                userRepositoryPort,
                passwordEncoderPort
        );
    }

    @Test
    void shouldCreateUserWithNormalizedDataAndEncodedPassword() {
        String name = "  Thiago Andrade  ";
        String email = "  THIAGO@EXAMPLE.COM  ";
        String password = "Senha123";
        String passwordHash = "$2a$10$hash-de-teste";

        User savedUser = new User(
                1L,
                "Thiago Andrade",
                "thiago@example.com",
                passwordHash
        );

        when(userRepositoryPort.existsByEmail("thiago@example.com")).thenReturn(false);

        when(passwordEncoderPort.encode(password)).thenReturn(passwordHash);

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = createUserService.createUser(
                name,
                email,
                password
        );

        assertSame(savedUser, result);

        verify(userRepositoryPort).existsByEmail("thiago@example.com");

        verify(passwordEncoderPort).encode(password);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepositoryPort).save(userCaptor.capture());

        User userSentToRepository = userCaptor.getValue();

        assertEquals("Thiago Andrade", userSentToRepository.getName());

        assertEquals("thiago@example.com", userSentToRepository.getEmail());

        assertEquals(passwordHash, userSentToRepository.getPasswordHash());
    }

    @Test
    void shouldRejectDuplicatedEmailBeforeEncodingPassword() {
        String email = "  THIAGO@EXAMPLE.COM  ";

        when(userRepositoryPort.existsByEmail("thiago@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class, () -> createUserService.createUser("Thiago Andrade", email, "Senha123"));

        assertEquals("Já existe um usuário cadastrado com este e-mail", exception.getMessage());

        verify(userRepositoryPort).existsByEmail("thiago@example.com");

        verifyNoInteractions(passwordEncoderPort);

        verify(userRepositoryPort, never()).save(any(User.class));
    }
}