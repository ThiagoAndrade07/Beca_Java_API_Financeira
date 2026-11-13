package com.becajava.transactionservice.application.exception;

public class AccountServiceUnavailableException extends RuntimeException {

    public AccountServiceUnavailableException(Throwable cause) {
        super("O serviço de saldo e limites está indisponível", cause);
    }
}