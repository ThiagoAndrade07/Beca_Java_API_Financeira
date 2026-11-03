package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.exception.EmailAlreadyExistsException;
import com.becajava.userservice.domain.exception.UserNotFoundException;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.in.UpdateUserUseCase;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;


    public UpdateUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User updateUser(Long id, String name, String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        User existingUser = userRepositoryPort.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (!existingUser.getEmail().equals(normalizedEmail)) {
            if (userRepositoryPort.existsByEmail(normalizedEmail)) {
                throw new EmailAlreadyExistsException();
            }
        }

        User updateUser = new User(
                existingUser.getId(),
                name.trim(),
                normalizedEmail,
                existingUser.getPasswordHash()
        );

        return userRepositoryPort.save(updateUser);

    }
}
