package com.becajava.transactionservice.port.out;

import com.becajava.transactionservice.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findByIdAndOwnerUserId(
            Long id,
            Long ownerUserId
    );

    List<Transaction> findAllByOwnerUserId(Long ownerUserId);
}