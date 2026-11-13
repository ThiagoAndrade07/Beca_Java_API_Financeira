package com.becajava.transactionservice.application.exception;

public class ExchangeRateNotFoundException extends RuntimeException {

    public ExchangeRateNotFoundException(String currency) {
        super("Não foi encontrada cotação para a moeda: " + currency);
    }
}