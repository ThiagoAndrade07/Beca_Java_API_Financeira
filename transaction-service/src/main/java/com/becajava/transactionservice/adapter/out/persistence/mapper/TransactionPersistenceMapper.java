package com.becajava.transactionservice.adapter.out.persistence.mapper;

import com.becajava.transactionservice.adapter.out.persistence.entity.TransactionJpaEntity;
import com.becajava.transactionservice.domain.model.ExchangeQuote;
import com.becajava.transactionservice.domain.model.ExchangeSnapshot;
import com.becajava.transactionservice.domain.model.Transaction;

public class TransactionPersistenceMapper {

    public static TransactionJpaEntity toJpaEntity(Transaction transaction) {
        TransactionJpaEntity entity = new TransactionJpaEntity(
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

        ExchangeSnapshot snapshot =
                transaction.getExchangeSnapshot();

        if (snapshot == null) {
            entity.attachExchangeData(
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Conversão não registrada"
            );

            return entity;
        }

        if (!snapshot.isAvailable()) {
            entity.attachExchangeData(
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    snapshot.getUnavailableReason()
            );

            return entity;
        }

        ExchangeQuote usd = snapshot.getUsd();
        ExchangeQuote eur = snapshot.getEur();

        entity.attachExchangeData(
                true,
                usd.getRate(),
                usd.getConvertedAmount(),
                usd.getQuoteDate(),
                eur.getRate(),
                eur.getConvertedAmount(),
                eur.getQuoteDate(),
                null
        );

        return entity;
    }

    public static Transaction toDomain(TransactionJpaEntity entity) {
        Transaction transaction = new Transaction(
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

        ExchangeSnapshot snapshot;

        if (entity.isExchangeAvailable()) {
            ExchangeQuote usd = new ExchangeQuote(
                    "USD",
                    entity.getUsdRate(),
                    entity.getAmountInUsd(),
                    entity.getUsdQuoteDate()
            );

            ExchangeQuote eur = new ExchangeQuote(
                    "EUR",
                    entity.getEurRate(),
                    entity.getAmountInEur(),
                    entity.getEurQuoteDate()
            );

            snapshot = ExchangeSnapshot.available(usd, eur);

        } else {
            String reason =
                    entity.getExchangeUnavailableReason() == null
                            ? "Conversão indisponível"
                            : entity.getExchangeUnavailableReason();

            snapshot = ExchangeSnapshot.unavailable(reason);
        }

        transaction.attachExchangeSnapshot(snapshot);

        return transaction;
    }
}