package com.becajava.transactionservice.adapter.out.persistence.mapper;

import com.becajava.transactionservice.adapter.out.persistence.entity.TransactionJpaEntity;
import com.becajava.transactionservice.domain.model.Transaction;

public class TransactionPersistenceMapper {

    public static TransactionJpaEntity toJpaEntity(Transaction transaction) {
        return new TransactionJpaEntity(
                transaction.getId(),
                transaction.getOwnerUserId(),
                transaction.getDestinationUserId(),
                transaction.getAmount(),
                transaction.getOccurredAt(),
                transaction.getDescription(),
                transaction.getCategory(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getRejectionReason(),
                transaction.getReversedTransactionId()
        );
    }

    public static Transaction toDomain(TransactionJpaEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getOwnerUserId(),
                entity.getDestinationUserId(),
                entity.getAmount(),
                entity.getOccurredAt(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getType(),
                entity.getStatus(),
                entity.getRejectionReason(),
                entity.getReversedTransactionId()
        );
    }
}