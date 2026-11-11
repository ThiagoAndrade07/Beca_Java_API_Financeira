package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.domain.exception.TransactionNotFoundException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetTransactionByIdServiceTest {

    private TransactionRepositoryPort transactionRepositoryPort;
    private GetTransactionByIdService service;

    @BeforeEach
    void setUp() {
        transactionRepositoryPort = mock(TransactionRepositoryPort.class);

        service = new GetTransactionByIdService(transactionRepositoryPort);
    }

    @Test
    void shouldReturnTransactionBelongingToOwner() {
        Transaction transaction = mock(Transaction.class);

        when(transactionRepositoryPort.findByIdAndOwnerUserId(10L, 7L)).thenReturn(Optional.of(transaction));

        Transaction result = service.getById(10L, 7L);

        assertSame(transaction, result);

        verify(transactionRepositoryPort).findByIdAndOwnerUserId(10L, 7L);
    }

    @Test
    void shouldThrowWhenTransactionDoesNotBelongToOwner() {
        when(transactionRepositoryPort.findByIdAndOwnerUserId(10L, 8L)).thenReturn(Optional.empty());

        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> service.getById(10L, 8L));

        assertEquals("Transação não encontrada com o id: 10", exception.getMessage());
    }
}