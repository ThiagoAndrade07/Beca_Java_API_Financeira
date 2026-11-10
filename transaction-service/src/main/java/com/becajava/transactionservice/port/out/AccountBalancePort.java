package com.becajava.transactionservice.port.out;

import com.becajava.transactionservice.application.model.AccountBalance;

import java.util.Optional;

public interface AccountBalancePort {
    Optional<AccountBalance> findByUserId(Long userId);
}
