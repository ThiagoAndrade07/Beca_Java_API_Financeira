package com.becajava.userservice.port.out;

import com.becajava.userservice.domain.model.User;

import java.util.List;
import java.util.Optional;


public interface UserRepositoryPort {
    User save(User user);

    boolean existsByEmail(String email);

    List<User> listAll();

    Optional<User> findById(Long id);

    void deleteById(Long id);

}
