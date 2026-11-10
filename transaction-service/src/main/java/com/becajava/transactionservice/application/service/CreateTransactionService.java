package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.model.CreateTransactionCommand;
import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.CreateTransactionUseCase;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateTransactionService implements CreateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;

    public CreateTransactionService(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public Transaction createTransaction(CreateTransactionCommand command) {
        if (command.getType() != TransactionType.DEPOSIT) {
            throw new InvalidTransactionException("Tipo de transação ainda não suportado");
        }

        Transaction transaction = Transaction.createDeposit(
                command.getOwnerUserId(),
                command.getAmount(),
                command.getDescription(),
                command.getCategory()
        );

        return transactionRepositoryPort.save(transaction);
    }
}
