package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListTransactionsServiceTest {

    private TransactionRepositoryPort transactionRepositoryPort;
    private ListTransactionsService service;

    @BeforeEach
    void setUp() {
        transactionRepositoryPort = mock(TransactionRepositoryPort.class);

        service = new ListTransactionsService(transactionRepositoryPort);
    }

    @Test
    void shouldListTransactionsUsingTypeAndPeriod() {
        LocalDate startDate = LocalDate.of(2026, 11, 1);

        LocalDate endDate = LocalDate.of(2026, 11, 30);

        Transaction transaction = mock(Transaction.class);

        when(
                transactionRepositoryPort
                        .findAllByOwnerUserIdAndFilters(
                                7L,
                                TransactionType.PURCHASE,
                                startDate.atStartOfDay(),
                                endDate.plusDays(1).atStartOfDay()
                        )
        ).thenReturn(List.of(transaction));

        List<Transaction> result =
                service.listByOwnerUserId(
                        7L,
                        TransactionType.PURCHASE,
                        startDate,
                        endDate
                );

        assertEquals(1, result.size());
        assertSame(transaction, result.getFirst());

        verify(transactionRepositoryPort)
                .findAllByOwnerUserIdAndFilters(
                        7L,
                        TransactionType.PURCHASE,
                        startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay()
                );
    }

    @Test
    void shouldRejectPeriodWhenStartDateIsAfterEndDate() {
        LocalDate startDate = LocalDate.of(2026, 11, 30);

        LocalDate endDate = LocalDate.of(2026, 11, 1);

        InvalidTransactionException exception =
                assertThrows(
                        InvalidTransactionException.class,
                        () -> service.listByOwnerUserId(
                                7L,
                                null,
                                startDate,
                                endDate
                        )
                );

        assertEquals("A data inicial não pode ser posterior à data final", exception.getMessage());

        verifyNoInteractions(transactionRepositoryPort);
    }
}