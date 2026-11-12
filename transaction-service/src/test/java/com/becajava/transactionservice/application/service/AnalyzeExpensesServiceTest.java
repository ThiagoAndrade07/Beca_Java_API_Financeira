package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.model.ExpenseGrouping;
import com.becajava.transactionservice.application.model.ExpenseSummary;
import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionCategory;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AnalyzeExpensesServiceTest {

    private TransactionRepositoryPort transactionRepositoryPort;
    private AnalyzeExpensesService analyzeExpensesService;

    @BeforeEach
    void setUp() {
        transactionRepositoryPort = mock(TransactionRepositoryPort.class);

        analyzeExpensesService = new AnalyzeExpensesService(transactionRepositoryPort);
    }

    @Test
    void shouldGroupOnlyApprovedExpensesByCategory() {
        List<Transaction> transactions = List.of(
                transaction(
                        "100.00",
                        TransactionCategory.FOOD,
                        TransactionType.PURCHASE,
                        TransactionStatus.APPROVED
                ),
                transaction(
                        "50.00",
                        TransactionCategory.FOOD,
                        TransactionType.PURCHASE,
                        TransactionStatus.APPROVED
                ),
                transaction(
                        "80.00",
                        TransactionCategory.HEALTH,
                        TransactionType.WITHDRAWAL,
                        TransactionStatus.APPROVED
                ),
                transaction(
                        "1000.00",
                        TransactionCategory.INCOME,
                        TransactionType.DEPOSIT,
                        TransactionStatus.APPROVED
                ),
                transaction(
                        "500.00",
                        TransactionCategory.FOOD,
                        TransactionType.PURCHASE,
                        TransactionStatus.REJECTED
                )
        );

        when(
                transactionRepositoryPort
                        .findAllByOwnerUserIdAndFilters(
                                eq(1L),
                                isNull(),
                                any(LocalDateTime.class),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(transactions);

        List<ExpenseSummary> result =
                analyzeExpensesService.analyze(
                        1L,
                        ExpenseGrouping.CATEGORY,
                        null,
                        null
                );

        assertEquals(2, result.size());

        assertEquals("FOOD", result.get(0).getGroup());
        assertEquals(new BigDecimal("150.00"), result.get(0).getTotal());

        assertEquals("HEALTH", result.get(1).getGroup());
        assertEquals(new BigDecimal("80.00"), result.get(1).getTotal());
    }

    @Test
    void shouldRejectInvalidPeriod() {
        LocalDate startDate = LocalDate.of(2026, 11, 30);
        LocalDate endDate = LocalDate.of(2026, 11, 1);

        InvalidTransactionException exception =
                assertThrows(
                        InvalidTransactionException.class,
                        () -> analyzeExpensesService.analyze(
                                1L,
                                ExpenseGrouping.MONTH,
                                startDate,
                                endDate
                        )
                );

        assertEquals("A data inicial não pode ser posterior à data final", exception.getMessage());

        verifyNoInteractions(transactionRepositoryPort);
    }

    private Transaction transaction(String amount, TransactionCategory category, TransactionType type, TransactionStatus status) {
        return new Transaction(
                1L,
                1L,
                null,
                new BigDecimal(amount),
                LocalDateTime.of(2026, 11, 12, 10, 0),
                "Transação de teste",
                category,
                type,
                status,
                status == TransactionStatus.REJECTED
                        ? "Rejeição de teste"
                        : null,
                null
        );
    }
}