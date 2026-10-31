package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.exception.EmailAlreadyExistsException;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.in.CreateUserUseCase;
import com.becajava.userservice.port.out.PasswordEncoderPort;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public CreateUserService(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public User createUser(String name, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if(userRepositoryPort.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException();
        }

        String passwordHash = passwordEncoderPort.encode(password);

        User user = new User(
                name.trim(),
                normalizedEmail,
                passwordHash
        );

        return userRepositoryPort.save(user);
    }

}
