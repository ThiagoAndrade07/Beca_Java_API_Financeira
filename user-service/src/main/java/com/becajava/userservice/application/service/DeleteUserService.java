package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.exception.UserNotFoundException;
import com.becajava.userservice.port.in.DeleteUserUseCase;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public DeleteUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void deleteUser(Long id) {
        userRepositoryPort.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        userRepositoryPort.deleteById(id);
    }

}
