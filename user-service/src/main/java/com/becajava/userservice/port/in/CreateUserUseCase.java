package com.becajava.userservice.port.in;

import com.becajava.userservice.domain.model.User;

public interface CreateUserUseCase {

    User createUser(String name, String email, String password);
}
