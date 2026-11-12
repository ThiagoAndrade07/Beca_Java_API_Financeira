package com.becajava.transactionservice.adapter.in.web.mapper;

import com.becajava.transactionservice.adapter.in.web.dto.AccountBalanceResponse;
import com.becajava.transactionservice.application.model.AccountBalance;

public final class AccountBalanceWebMapper {

    private AccountBalanceWebMapper() {
    }

    public static AccountBalanceResponse toResponse(AccountBalance accountBalance) {
        return new AccountBalanceResponse(
                accountBalance.getUserId(),
                accountBalance.getBalance(),
                accountBalance.getDailyLimit(),
                accountBalance.getTransactionLimit()
        );
    }
}