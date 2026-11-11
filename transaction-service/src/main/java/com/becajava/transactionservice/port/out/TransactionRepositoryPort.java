package com.becajava.transactionservice.port.out;

import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findByIdAndOwnerUserId(
            Long id,
            Long ownerUserId
    );

    List<Transaction> findAllByOwnerUserId(Long ownerUserId);

    BigDecimal sumApprovedOutgoingAmountByOwnerUserIdBetween(Long ownerUserId, LocalDateTime start, LocalDateTime end);

    List<Transaction> findAllByOwnerUserIdAndFilters(Long ownerUserId, TransactionType type, LocalDateTime start, LocalDateTime end);
}