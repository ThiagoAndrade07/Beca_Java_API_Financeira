package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.ListTransactionsUseCase;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListTransactionsService implements ListTransactionsUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;

    public ListTransactionsService(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public List<Transaction> listByOwnerUserId(Long ownerUserId, TransactionType type, LocalDate startDate, LocalDate endDate) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidTransactionException(
                    "A data inicial não pode ser posterior à data final"
            );
        }

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;

        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

        return transactionRepositoryPort.findAllByOwnerUserIdAndFilters(ownerUserId, type, start, end);
    }
}