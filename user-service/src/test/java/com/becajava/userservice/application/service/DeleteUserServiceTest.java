package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.exception.UserNotFoundException;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteUserServiceTest {

    private UserRepositoryPort userRepositoryPort;
    private DeleteUserService deleteUserService;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);

        deleteUserService = new DeleteUserService(userRepositoryPort);
    }

    @Test
    void shouldDeleteUserWhenIdExists() {
        Long userId = 1L;

        User existingUser = new User(
                userId,
                "Maria Silva",
                "maria@example.com",
                "password-hash"
        );

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> deleteUserService.deleteUser(userId));

        verify(userRepositoryPort).findById(userId);
        verify(userRepositoryPort).deleteById(userId);
    }

    @Test
    void shouldThrowExceptionAndNotDeleteWhenIdDoesNotExist() {Long userId = 999L;

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> deleteUserService.deleteUser(userId));

        assertEquals("Usuário não encontrado com o id: 999", exception.getMessage());

        verify(userRepositoryPort).findById(userId);

        verify(userRepositoryPort, never()).deleteById(userId);
    }
}