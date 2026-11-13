package com.becajava.transactionservice.adapter.in.web.mapper;

import com.becajava.transactionservice.adapter.in.web.dto.TransactionResponse;
import com.becajava.transactionservice.domain.model.Transaction;

public final class TransactionWebMapper {

    private TransactionWebMapper() {
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getOwnerUserId(),
                transaction.getDestinationUserId(),
                transaction.getAmount(),
                transaction.getOccurredAt(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getStatus(),
                transaction.getRejectionReason(),
                transaction.getReversedTransactionId(),
                ExchangeSnapshotWebMapper.toResponse((transaction.getExchangeSnapshot()))
        );
    }
}
