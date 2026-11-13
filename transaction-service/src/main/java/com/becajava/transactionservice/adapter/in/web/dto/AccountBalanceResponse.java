package com.becajava.transactionservice.adapter.in.web.dto;

import java.math.BigDecimal;

public class AccountBalanceResponse {

    private final Long userId;
    private final BigDecimal balance;
    private final BigDecimal dailyLimit;
    private final BigDecimal transactionLimit;

    public AccountBalanceResponse(
            Long userId,
            BigDecimal balance,
            BigDecimal dailyLimit,
            BigDecimal transactionLimit
    ) {
        this.userId = userId;
        this.balance = balance;
        this.dailyLimit = dailyLimit;
        this.transactionLimit = transactionLimit;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public BigDecimal getTransactionLimit() {
        return transactionLimit;
    }
}