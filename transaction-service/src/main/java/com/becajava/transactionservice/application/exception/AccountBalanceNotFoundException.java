package com.becajava.transactionservice.application.exception;

public class AccountBalanceNotFoundException extends RuntimeException {

    public AccountBalanceNotFoundException(Long userId) {
        super("Conta de saldo e limites não encontrada para o usuário: " + userId);
    }
}