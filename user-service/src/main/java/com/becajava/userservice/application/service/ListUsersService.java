package com.becajava.userservice.application.service;

import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.in.ListUsersUseCase;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUsersService implements ListUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public ListUsersService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public List<User> listUsers() {
        return userRepositoryPort.listAll();
    }

}
