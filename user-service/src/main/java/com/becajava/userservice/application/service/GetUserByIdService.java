package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.exception.UserNotFoundException;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.in.GetUserByIdUseCase;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public GetUserByIdService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User getUserById(Long id) {
        return userRepositoryPort.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

}
