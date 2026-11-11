package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.domain.exception.TransactionNotFoundException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.port.in.GetTransactionByIdUseCase;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class GetTransactionByIdService implements GetTransactionByIdUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;

    public GetTransactionByIdService(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public Transaction getById(Long id, Long ownerUserId) {
        return transactionRepositoryPort
                .findByIdAndOwnerUserId(id, ownerUserId)
                .orElseThrow(
                        () -> new TransactionNotFoundException(id)
                );
    }
}