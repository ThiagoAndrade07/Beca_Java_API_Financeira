package com.becajava.userservice.application.service;

import com.becajava.userservice.application.model.ImportUserCommand;
import com.becajava.userservice.application.model.ImportUsersResult;
import com.becajava.userservice.domain.exception.EmailAlreadyExistsException;
import com.becajava.userservice.port.in.CreateUserUseCase;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ImportUsersServiceTest {

    private CreateUserUseCase createUserUseCase;
    private ValidatorFactory validatorFactory;
    private ImportUsersService importUsersService;

    @BeforeEach
    void setUp() {
        createUserUseCase = mock(CreateUserUseCase.class);

        validatorFactory =
                Validation.buildDefaultValidatorFactory();

        Validator validator = validatorFactory.getValidator();

        importUsersService = new ImportUsersService(
                createUserUseCase,
                validator
        );
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldImportValidUsers() {
        List<ImportUserCommand> commands = List.of(
                new ImportUserCommand(
                        2,
                        "Maria Silva",
                        "maria@example.com",
                        "Senha123"
                ),
                new ImportUserCommand(
                        3,
                        "João Souza",
                        "joao@example.com",
                        "OutraSenha456"
                )
        );

        ImportUsersResult result =
                importUsersService.importUsers(commands);

        assertEquals(2, result.getTotalLines());
        assertEquals(2, result.getImportedUsers());
        assertEquals(0, result.getFailedLines());
        assertTrue(result.getErrors().isEmpty());

        verify(createUserUseCase).createUser(
                "Maria Silva",
                "maria@example.com",
                "Senha123"
        );

        verify(createUserUseCase).createUser(
                "João Souza",
                "joao@example.com",
                "OutraSenha456"
        );
    }

    @Test
    void shouldReportInvalidUserWithoutTryingToCreateIt() {
        ImportUserCommand invalidCommand =
                new ImportUserCommand(
                        2,
                        "",
                        "email-invalido",
                        "123"
                );

        ImportUsersResult result =
                importUsersService.importUsers(
                        List.of(invalidCommand)
                );

        assertEquals(1, result.getTotalLines());
        assertEquals(0, result.getImportedUsers());
        assertEquals(1, result.getFailedLines());
        assertEquals(2, result.getErrors().get(0).getLineNumber());

        assertTrue(
                result.getErrors()
                        .get(0)
                        .getMessage()
                        .contains("O nome é obrigatório")
        );

        verifyNoInteractions(createUserUseCase);
    }

    @Test
    void shouldReportDuplicateEmailAndContinueImporting() {
        ImportUserCommand duplicateCommand =
                new ImportUserCommand(
                        2,
                        "Maria Silva",
                        "maria@example.com",
                        "Senha123"
                );

        ImportUserCommand validCommand =
                new ImportUserCommand(
                        3,
                        "João Souza",
                        "joao@example.com",
                        "OutraSenha456"
                );

        doThrow(new EmailAlreadyExistsException())
                .when(createUserUseCase)
                .createUser(
                        "Maria Silva",
                        "maria@example.com",
                        "Senha123"
                );

        ImportUsersResult result =
                importUsersService.importUsers(
                        List.of(duplicateCommand, validCommand)
                );

        assertEquals(2, result.getTotalLines());
        assertEquals(1, result.getImportedUsers());
        assertEquals(1, result.getFailedLines());

        assertEquals(
                2,
                result.getErrors().get(0).getLineNumber()
        );

        assertEquals(
                "Já existe um usuário cadastrado com este e-mail",
                result.getErrors().get(0).getMessage()
        );

        verify(createUserUseCase).createUser(
                "João Souza",
                "joao@example.com",
                "OutraSenha456"
        );
    }
}