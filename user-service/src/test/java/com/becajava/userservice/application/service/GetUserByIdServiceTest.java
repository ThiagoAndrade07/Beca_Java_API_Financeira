package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.exception.UserNotFoundException;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetUserByIdServiceTest {

    private UserRepositoryPort userRepositoryPort;
    private GetUserByIdService getUserByIdService;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);

        getUserByIdService = new GetUserByIdService(userRepositoryPort);
    }

    @Test
    void shouldReturnUserWhenIdExists() {
        Long userId = 1L;

        User existingUser = new User(
                userId,
                "Maria Silva",
                "maria@example.com",
                "password-hash"
        );

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(existingUser));

        User result = getUserByIdService.getUserById(userId);

        assertSame(existingUser, result);
        assertEquals(userId, result.getId());
        assertEquals("Maria Silva", result.getName());
        assertEquals("maria@example.com", result.getEmail());

        verify(userRepositoryPort).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenIdDoesNotExist() {
        Long userId = 999L;

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> getUserByIdService.getUserById(userId));

        assertEquals("Usuário não encontrado com o id: 999", exception.getMessage());

        verify(userRepositoryPort).findById(userId);
    }
}