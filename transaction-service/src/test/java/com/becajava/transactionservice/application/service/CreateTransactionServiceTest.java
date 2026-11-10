package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.model.CreateTransactionCommand;
import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionCategory;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CreateTransactionServiceTest {

    private TransactionRepositoryPort transactionRepositoryPort;
    private CreateTransactionService createTransactionService;

    @BeforeEach
    void setUp() {
        transactionRepositoryPort = mock(TransactionRepositoryPort.class);

        createTransactionService = new CreateTransactionService(transactionRepositoryPort);
    }

    @Test
    void shouldCreateAndSaveDeposit() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(
                        7L,
                        new BigDecimal("500.00"),
                        "Depósito inicial",
                        TransactionCategory.INCOME,
                        TransactionType.DEPOSIT,
                        null
                );

        when(transactionRepositoryPort.save(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction transaction =
                            invocation.getArgument(0);

                    return new Transaction(
                            10L,
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
                });

        Transaction result = createTransactionService.createTransaction(command);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepositoryPort).save(transactionCaptor.capture());

        Transaction transactionSentToRepository = transactionCaptor.getValue();

        assertEquals(7L, transactionSentToRepository.getOwnerUserId());

        assertEquals(new BigDecimal("500.00"), transactionSentToRepository.getAmount());

        assertEquals("Depósito inicial", transactionSentToRepository.getDescription());

        assertEquals(TransactionType.DEPOSIT, transactionSentToRepository.getType());

        assertEquals(TransactionStatus.APPROVED, transactionSentToRepository.getStatus());

        assertEquals(TransactionCategory.INCOME, transactionSentToRepository.getCategory());

        assertNull(transactionSentToRepository.getDestinationUserId());

        assertNull(transactionSentToRepository.getRejectionReason());

        assertNull(transactionSentToRepository.getReversedTransactionId());

        assertEquals(10L, result.getId());
    }

    @Test
    void shouldRejectUnsupportedTransactionTypeWithoutSaving() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(
                        7L,
                        new BigDecimal("100.00"),
                        "Compra ainda não implementada",
                        TransactionCategory.FOOD,
                        TransactionType.PURCHASE,
                        null
                );

        InvalidTransactionException exception =
                assertThrows(
                        InvalidTransactionException.class,
                        () -> createTransactionService
                                .createTransaction(command)
                );

        assertEquals("Tipo de transação ainda não suportado", exception.getMessage());

        verifyNoInteractions(transactionRepositoryPort);
    }
}