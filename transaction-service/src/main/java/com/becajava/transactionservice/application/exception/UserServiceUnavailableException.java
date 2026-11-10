package com.becajava.transactionservice.application.exception;

public class UserServiceUnavailableException extends RuntimeException {

    public UserServiceUnavailableException(Throwable cause) {
        super("O serviço de usuários está indisponível", cause);
    }
}