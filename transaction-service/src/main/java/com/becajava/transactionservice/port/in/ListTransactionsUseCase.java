package com.becajava.transactionservice.port.in;

import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface ListTransactionsUseCase {

    List<Transaction> listByOwnerUserId(
            Long ownerUserId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate
    );
}