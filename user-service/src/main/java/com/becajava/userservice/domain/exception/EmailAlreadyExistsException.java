package com.becajava.userservice.domain.exception;

public class EmailAlreadyExistsException  extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("Já existe um usuário cadastrado com este e-mail");
    }
}
