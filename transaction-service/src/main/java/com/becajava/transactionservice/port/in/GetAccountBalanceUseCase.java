package com.becajava.transactionservice.port.in;

import com.becajava.transactionservice.application.model.AccountBalance;

public interface GetAccountBalanceUseCase {

    AccountBalance getByUserId(Long userId);
}