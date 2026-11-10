package com.becajava.transactionservice.port.in;

import com.becajava.transactionservice.application.model.CreateTransactionCommand;
import com.becajava.transactionservice.domain.model.Transaction;

public interface CreateTransactionUseCase {

    Transaction createTransaction(CreateTransactionCommand command);
}