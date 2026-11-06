package com.becajava.userservice.application.service;

import com.becajava.userservice.application.model.ImportUserCommand;
import com.becajava.userservice.application.model.ImportUserError;
import com.becajava.userservice.application.model.ImportUsersResult;
import com.becajava.userservice.domain.exception.EmailAlreadyExistsException;
import com.becajava.userservice.port.in.CreateUserUseCase;
import com.becajava.userservice.port.in.ImportUsersUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImportUsersService implements ImportUsersUseCase {

    private final CreateUserUseCase createUserUseCase;
    private final Validator validator;

    public ImportUsersService(CreateUserUseCase createUserUseCase, Validator validator) {
        this.createUserUseCase = createUserUseCase;
        this.validator = validator;
    }

    @Override
    public ImportUsersResult importUsers(List<ImportUserCommand> commands) {

        List<ImportUserError> errors = new ArrayList<>();

        int importedUsers = 0;

        for (ImportUserCommand command : commands) {

            Set<ConstraintViolation<ImportUserCommand>> violations = validator.validate(command);

            if (!violations.isEmpty()) {

                String errorMessage = violations.stream().map(ConstraintViolation::getMessage).distinct().sorted().collect(Collectors.joining("; "));

                ImportUserError error = new ImportUserError(command.getLineNumber(), errorMessage);

                errors.add(error);

                continue;
            }

            try {
                createUserUseCase.createUser(command.getName(), command.getEmail(), command.getPassword());

                importedUsers++;
            } catch (EmailAlreadyExistsException exception) {
                ImportUserError error = new ImportUserError(command.getLineNumber(), exception.getMessage());

                errors.add(error);
            }

        }

        return new ImportUsersResult(commands.size(), importedUsers, errors);
    }

}
