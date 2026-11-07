package com.becajava.userservice.application.validation;

import com.becajava.userservice.adapter.in.web.dto.CreateUserRequest;
import com.becajava.userservice.application.model.ImportUserCommand;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Utf8ByteSizeValidationTest {

    private static final String BYTE_ERROR_MESSAGE = "A senha deve ter no máximo 72 bytes em UTF-8";

    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptPasswordWithSeventyTwoAsciiBytes() {
        String password = "a".repeat(72);

        CreateUserRequest request = validRequest(password);

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertEquals(72, password.getBytes(StandardCharsets.UTF_8).length);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectHttpPasswordLargerThanSeventyTwoUtf8Bytes() {
        String password = "á".repeat(37);

        CreateUserRequest request = validRequest(password);

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertEquals(74, password.getBytes(StandardCharsets.UTF_8).length);

        assertTrue(
                violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .anyMatch(BYTE_ERROR_MESSAGE::equals)
        );
    }

    @Test
    void shouldRejectCsvPasswordLargerThanSeventyTwoUtf8Bytes() {
        String password = "á".repeat(37);

        ImportUserCommand command = new ImportUserCommand(
                2,
                "Maria Silva",
                "maria@example.com",
                password
        );

        Set<ConstraintViolation<ImportUserCommand>> violations = validator.validate(command);

        assertEquals(74, password.getBytes(StandardCharsets.UTF_8).length);

        assertTrue(violations.stream().map(ConstraintViolation::getMessage).anyMatch(BYTE_ERROR_MESSAGE::equals));
    }

    private CreateUserRequest validRequest(String password) {
        CreateUserRequest request = new CreateUserRequest();

        request.setName("Maria Silva");
        request.setEmail("maria@example.com");
        request.setPassword(password);

        return request;
    }
}