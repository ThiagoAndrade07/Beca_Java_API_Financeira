package com.becajava.userservice.port.out;

import com.becajava.userservice.domain.model.User;

public interface UserRepositoryPort {
    User save(User user);
    boolean existsByEmail(String email);
}
