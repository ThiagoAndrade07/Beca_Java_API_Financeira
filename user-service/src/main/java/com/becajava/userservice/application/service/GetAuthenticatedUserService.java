package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.in.GetAuthenticatedUserUseCase;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class GetAuthenticatedUserService implements GetAuthenticatedUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public GetAuthenticatedUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User getAuthenticatedUser(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        User userFound = userRepositoryPort.findByEmail(normalizedEmail).orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado"));

        return userFound;
    }
}
