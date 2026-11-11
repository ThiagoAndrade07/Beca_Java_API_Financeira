package com.becajava.transactionservice.port.in;

import com.becajava.transactionservice.domain.model.Transaction;

public interface GetTransactionByIdUseCase {

    Transaction getById(
            Long id,
            Long ownerUserId
    );
}