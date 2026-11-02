package com.becajava.userservice.domain.exception;


public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuário não encontrado com o id: " + id);
    }
}
