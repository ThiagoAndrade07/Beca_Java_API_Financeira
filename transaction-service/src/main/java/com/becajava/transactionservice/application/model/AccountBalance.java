package com.becajava.transactionservice.application.model;

import java.math.BigDecimal;

public class AccountBalance {
    private Long userId;
    private BigDecimal balance;
    private BigDecimal dailyLimit;
    private BigDecimal transactionLimit;

    public AccountBalance() {
    }

    public AccountBalance(Long userId, BigDecimal balance, BigDecimal dailyLimit, BigDecimal transactionLimit) {
        this.userId = userId;
        this.balance = balance;
        this.dailyLimit = dailyLimit;
        this.transactionLimit = transactionLimit;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(BigDecimal transactionLimit) {
        this.transactionLimit = transactionLimit;
    }
}
