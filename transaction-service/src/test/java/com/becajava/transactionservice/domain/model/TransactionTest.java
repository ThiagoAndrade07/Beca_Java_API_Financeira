package com.becajava.transactionservice.domain.model;

import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    void shouldCreateApprovedDeposit() {
        Transaction transaction = Transaction.createDeposit(
                7L,
                new BigDecimal("500.00"),
                "  Depósito inicial  ",
                TransactionCategory.INCOME
        );

        assertEquals(7L, transaction.getOwnerUserId());
        assertEquals(new BigDecimal("500.00"), transaction.getAmount());
        assertEquals("Depósito inicial", transaction.getDescription());
        assertEquals(TransactionCategory.INCOME, transaction.getCategory());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());

        assertNotNull(transaction.getOccurredAt());
        assertNull(transaction.getDestinationUserId());
        assertNull(transaction.getRejectionReason());
        assertNull(transaction.getReversedTransactionId());
    }

    @Test
    void shouldRejectDepositWithInvalidCategory() {
        InvalidTransactionException exception = assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.createDeposit(
                        7L,
                        new BigDecimal("500.00"),
                        "Depósito inicial",
                        TransactionCategory.FOOD
                )
        );

        assertEquals(
                "A categoria de um depósito deve ser INCOME",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectDepositWithZeroOrNegativeAmount() {
        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.createDeposit(
                        7L,
                        BigDecimal.ZERO,
                        "Depósito com valor zero",
                        TransactionCategory.INCOME
                )
        );

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.createDeposit(
                        7L,
                        new BigDecimal("-10.00"),
                        "Depósito com valor negativo",
                        TransactionCategory.INCOME
                )
        );
    }

    @Test
    void shouldRejectDepositWithoutOwner() {
        InvalidTransactionException exception = assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.createDeposit(
                        null,
                        new BigDecimal("500.00"),
                        "Depósito sem dono",
                        TransactionCategory.INCOME
                )
        );

        assertEquals(
                "O usuário dono da transação é obrigatório",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectDepositWithoutDescription() {
        InvalidTransactionException exception = assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.createDeposit(
                        7L,
                        new BigDecimal("500.00"),
                        "   ",
                        TransactionCategory.INCOME
                )
        );

        assertEquals(
                "A descrição do depósito é obrigatória",
                exception.getMessage()
        );
    }
}
