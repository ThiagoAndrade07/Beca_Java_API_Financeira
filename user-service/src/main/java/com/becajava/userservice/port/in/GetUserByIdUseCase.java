package com.becajava.userservice.port.in;

import com.becajava.userservice.domain.model.User;

public interface GetUserByIdUseCase {
    User getUserById(Long id);
}
