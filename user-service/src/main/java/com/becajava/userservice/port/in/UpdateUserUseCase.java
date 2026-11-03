package com.becajava.userservice.port.in;

import com.becajava.userservice.domain.model.User;

public interface UpdateUserUseCase {
    User updateUser(Long id, String name, String email);
}
