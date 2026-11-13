package com.becajava.transactionservice.application.exception;

public class ExchangeServiceUnavailableException extends RuntimeException {

    public ExchangeServiceUnavailableException() {
        super("O serviço de câmbio está temporariamente indisponível");
    }

    public ExchangeServiceUnavailableException(Throwable cause) {
        super("O serviço de câmbio está temporariamente indisponível", cause);
    }
}