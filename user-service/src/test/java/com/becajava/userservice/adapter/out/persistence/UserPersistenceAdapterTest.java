package com.becajava.userservice.adapter.out.persistence;

import com.becajava.userservice.adapter.out.persistence.entity.UserJpaEntity;
import com.becajava.userservice.adapter.out.persistence.repository.UserJpaRepository;
import com.becajava.userservice.domain.exception.EmailAlreadyExistsException;
import com.becajava.userservice.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserPersistenceAdapterTest {

    private UserJpaRepository userJpaRepository;
    private UserPersistenceAdapter userPersistenceAdapter;

    @BeforeEach
    void setUp() {
        userJpaRepository = mock(UserJpaRepository.class);

        userPersistenceAdapter = new UserPersistenceAdapter(userJpaRepository);
    }

    @Test
    void shouldTranslateIntegrityViolationWhenEmailAlreadyExists() {
        User user = new User(
                "Maria Silva",
                "maria@example.com",
                "password-hash"
        );

        DataIntegrityViolationException databaseException = new DataIntegrityViolationException("Database constraint violation");

        when(userJpaRepository.saveAndFlush(any(UserJpaEntity.class))).thenThrow(databaseException);

        when(userJpaRepository.existsByEmail("maria@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> userPersistenceAdapter.save(user));

        assertEquals("Já existe um usuário cadastrado com este e-mail", exception.getMessage());

        verify(userJpaRepository).existsByEmail("maria@example.com");
    }

    @Test
    void shouldPreserveIntegrityViolationWhenEmailDoesNotExist() {
        User user = new User(
                "Maria Silva",
                "maria@example.com",
                "password-hash"
        );

        DataIntegrityViolationException databaseException = new DataIntegrityViolationException("Another database constraint violation");

        when(userJpaRepository.saveAndFlush(any(UserJpaEntity.class))).thenThrow(databaseException);

        when(userJpaRepository.existsByEmail("maria@example.com")).thenReturn(false);

        DataIntegrityViolationException result = assertThrows(DataIntegrityViolationException.class, () -> userPersistenceAdapter.save(user));

        assertSame(databaseException, result);

        verify(userJpaRepository).existsByEmail("maria@example.com");
    }
}