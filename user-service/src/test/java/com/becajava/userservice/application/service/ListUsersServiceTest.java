package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListUsersServiceTest {

    private UserRepositoryPort userRepositoryPort;
    private ListUsersService listUsersService;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);

        listUsersService = new ListUsersService(userRepositoryPort);
    }

    @Test
    void shouldReturnAllUsers() {
        User firstUser = new User(
                1L,
                "Maria Silva",
                "maria@example.com",
                "hash-maria"
        );

        User secondUser = new User(
                2L,
                "João Souza",
                "joao@example.com",
                "hash-joao"
        );

        List<User> repositoryUsers = List.of(firstUser, secondUser);

        when(userRepositoryPort.listAll()).thenReturn(repositoryUsers);

        List<User> result = listUsersService.listUsers();

        assertSame(repositoryUsers, result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Maria Silva", result.get(0).getName());
        assertEquals("maria@example.com", result.get(0).getEmail());

        assertEquals(2L, result.get(1).getId());
        assertEquals("João Souza", result.get(1).getName());
        assertEquals("joao@example.com", result.get(1).getEmail());

        verify(userRepositoryPort).listAll();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoUsers() {
        when(userRepositoryPort.listAll()).thenReturn(List.of());

        List<User> result = listUsersService.listUsers();

        assertTrue(result.isEmpty());

        verify(userRepositoryPort).listAll();
    }
}