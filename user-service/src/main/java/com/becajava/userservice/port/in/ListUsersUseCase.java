package com.becajava.userservice.port.in;

import com.becajava.userservice.domain.model.User;

import java.util.List;

public interface ListUsersUseCase {

    List<User> listUsers();
}
