package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.exception.AccountBalanceNotFoundException;
import com.becajava.transactionservice.application.model.AccountBalance;
import com.becajava.transactionservice.port.in.GetAccountBalanceUseCase;
import com.becajava.transactionservice.port.out.AccountBalancePort;
import org.springframework.stereotype.Service;

@Service
public class GetAccountBalanceService implements GetAccountBalanceUseCase {

    private final AccountBalancePort accountBalancePort;

    public GetAccountBalanceService(AccountBalancePort accountBalancePort) {
        this.accountBalancePort = accountBalancePort;
    }

    @Override
    public AccountBalance getByUserId(Long userId) {
        return accountBalancePort.findByUserId(userId).orElseThrow(() -> new AccountBalanceNotFoundException(userId));
    }
}
