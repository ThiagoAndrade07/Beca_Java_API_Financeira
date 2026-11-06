package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.exception.EmailAlreadyExistsException;
import com.becajava.userservice.domain.exception.UserNotFoundException;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateUserServiceTest {

    private UserRepositoryPort userRepositoryPort;
    private UpdateUserService updateUserService;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);

        updateUserService = new UpdateUserService(userRepositoryPort);
    }

    @Test
    void shouldUpdateUserAndPreservePasswordHash() {
        Long userId = 1L;
        String existingPasswordHash = "existing-password-hash";

        User existingUser = new User(
                userId,
                "Nome Antigo",
                "email.antigo@example.com",
                existingPasswordHash
        );

        User savedUser = new User(
                userId,
                "Nome Atualizado",
                "email.novo@example.com",
                existingPasswordHash
        );

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(existingUser));

        when(userRepositoryPort.existsByEmail("email.novo@example.com")).thenReturn(false);

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = updateUserService.updateUser(
                userId,
                "  Nome Atualizado  ",
                "  EMAIL.NOVO@EXAMPLE.COM  "
        );

        assertSame(savedUser, result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepositoryPort).save(userCaptor.capture());

        User userSentToRepository = userCaptor.getValue();

        assertEquals(userId, userSentToRepository.getId());
        assertEquals("Nome Atualizado", userSentToRepository.getName());
        assertEquals("email.novo@example.com", userSentToRepository.getEmail());
        assertEquals(existingPasswordHash, userSentToRepository.getPasswordHash());

        verify(userRepositoryPort).findById(userId);

        verify(userRepositoryPort).existsByEmail("email.novo@example.com");
    }

    @Test
    void shouldUpdateNameWithoutCheckingDuplicateWhenEmailIsUnchanged() {
        Long userId = 1L;

        User existingUser = new User(
                userId,
                "Nome Antigo",
                "maria@example.com",
                "password-hash"
        );

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(existingUser));

        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = updateUserService.updateUser(
                userId,
                "  Maria Atualizada  ",
                "  MARIA@EXAMPLE.COM  "
        );

        assertEquals("Maria Atualizada", result.getName());
        assertEquals("maria@example.com", result.getEmail());
        assertEquals("password-hash", result.getPasswordHash()
        );

        verify(userRepositoryPort, never()).existsByEmail(anyString());

        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        Long userId = 999L;

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> updateUserService.updateUser(
                        userId,
                        "Nome",
                        "email@example.com"
                )
        );

        assertEquals("Usuário não encontrado com o id: 999", exception.getMessage());

        verify(userRepositoryPort).findById(userId);

        verify(userRepositoryPort, never()).existsByEmail(anyString());

        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void shouldRejectEmailThatBelongsToAnotherUser() {
        Long userId = 1L;

        User existingUser = new User(
                userId,
                "Maria Silva",
                "maria@example.com",
                "password-hash"
        );

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(existingUser));

        when(userRepositoryPort.existsByEmail("email.existente@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> updateUserService.updateUser(
                        userId,
                        "Maria Silva",
                        "EMAIL.EXISTENTE@EXAMPLE.COM"
                )
        );

        assertEquals("Já existe um usuário cadastrado com este e-mail", exception.getMessage());

        verify(userRepositoryPort).findById(userId);

        verify(userRepositoryPort).existsByEmail("email.existente@example.com");

        verify(userRepositoryPort, never()).save(any(User.class));
    }
}