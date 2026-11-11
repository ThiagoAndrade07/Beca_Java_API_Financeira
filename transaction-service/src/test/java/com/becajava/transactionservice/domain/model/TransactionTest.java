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

        assertEquals("A categoria de um depósito deve ser INCOME", exception.getMessage());
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

        assertEquals("O usuário dono da transação é obrigatório", exception.getMessage());
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

        assertEquals("A descrição do depósito é obrigatória", exception.getMessage());
    }

    @Test
    void shouldCreateApprovedWithdrawal() {
        Transaction transaction = Transaction.createWithdrawal(
                7L,
                new BigDecimal("300.00"),
                "Retirada aprovada",
                TransactionCategory.OTHER,
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("200.00")
        );

        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertNull(transaction.getRejectionReason());
    }

    @Test
    void shouldRejectWithdrawalWithInsufficientBalance() {
        Transaction transaction = Transaction.createWithdrawal(
                7L,
                new BigDecimal("600.00"),
                "Retirada sem saldo",
                TransactionCategory.OTHER,
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("2000.00"),
                BigDecimal.ZERO
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals("Saldo insuficiente", transaction.getRejectionReason());
    }

    @Test
    void shouldRejectWithdrawalAboveTransactionLimit() {
        Transaction transaction = Transaction.createWithdrawal(
                7L,
                new BigDecimal("600.00"),
                "Retirada acima do limite",
                TransactionCategory.OTHER,
                new BigDecimal("2000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("2000.00"),
                BigDecimal.ZERO
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals("Valor acima do limite por transação", transaction.getRejectionReason());
    }

    @Test
    void shouldRejectWithdrawalAboveDailyLimit() {
        Transaction transaction = Transaction.createWithdrawal(
                7L,
                new BigDecimal("300.00"),
                "Retirada acima do limite diario",
                TransactionCategory.OTHER,
                new BigDecimal("2000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("800.00")
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals("Limite diário excedido", transaction.getRejectionReason());
    }

    @Test
    void shouldCreateApprovedTransferWhenBalanceAndLimitsAreAvailable() {
        Transaction transaction = Transaction.createTransfer(
                7L,
                8L,
                new BigDecimal("300.00"),
                "  Transferência para outro usuário  ",
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("200.00")
        );

        assertEquals(7L, transaction.getOwnerUserId());

        assertEquals(8L, transaction.getDestinationUserId());

        assertEquals(new BigDecimal("300.00"), transaction.getAmount());

        assertEquals("Transferência para outro usuário", transaction.getDescription());

        assertEquals(TransactionType.TRANSFER, transaction.getType());

        assertEquals(TransactionCategory.TRANSFER, transaction.getCategory());

        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());

        assertNotNull(transaction.getOccurredAt());
        assertNull(transaction.getRejectionReason());
        assertNull(transaction.getReversedTransactionId());
    }

    @Test
    void shouldRejectTransferWithInsufficientBalance() {
        Transaction transaction = Transaction.createTransfer(
                7L,
                8L,
                new BigDecimal("600.00"),
                "Transferência sem saldo",
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("2000.00"),
                BigDecimal.ZERO
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());

        assertEquals("Saldo insuficiente", transaction.getRejectionReason());

        assertEquals(TransactionType.TRANSFER, transaction.getType());

        assertEquals(8L, transaction.getDestinationUserId());
    }

    @Test
    void shouldRejectTransferAboveTransactionLimit() {
        Transaction transaction = Transaction.createTransfer(
                7L,
                8L,
                new BigDecimal("600.00"),
                "Transferência acima do limite",
                new BigDecimal("2000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("2000.00"),
                BigDecimal.ZERO
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());

        assertEquals("Valor acima do limite por transação", transaction.getRejectionReason());
    }

    @Test
    void shouldRejectTransferAboveDailyLimit() {
        Transaction transaction = Transaction.createTransfer(
                7L,
                8L,
                new BigDecimal("300.00"),
                "Transferência acima do limite diário",
                new BigDecimal("2000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("800.00")
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());

        assertEquals("Limite diário excedido", transaction.getRejectionReason());
    }

    @Test
    void shouldCreateApprovedPurchase() {
        Transaction transaction = Transaction.createPurchase(
                7L,
                new BigDecimal("300.00"),
                "  Compra no mercado  ",
                TransactionCategory.FOOD,
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("200.00")
        );

        assertEquals(7L, transaction.getOwnerUserId());
        assertEquals(new BigDecimal("300.00"), transaction.getAmount());
        assertEquals("Compra no mercado", transaction.getDescription());
        assertEquals(TransactionCategory.FOOD, transaction.getCategory());
        assertEquals(TransactionType.PURCHASE, transaction.getType());
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());

        assertNull(transaction.getDestinationUserId());
        assertNull(transaction.getRejectionReason());
    }

    @Test
    void shouldRejectPurchaseWithInvalidCategory() {
        InvalidTransactionException exception =
                assertThrows(
                        InvalidTransactionException.class,
                        () -> Transaction.createPurchase(
                                7L,
                                new BigDecimal("300.00"),
                                "Compra com categoria inválida",
                                TransactionCategory.INCOME,
                                new BigDecimal("1000.00"),
                                new BigDecimal("500.00"),
                                new BigDecimal("1000.00"),
                                BigDecimal.ZERO
                        )
                );

        assertEquals("A compra deve utilizar uma categoria de despesa", exception.getMessage());
    }

    @Test
    void shouldSaveRejectedPurchaseWithInsufficientBalance() {
        Transaction transaction = Transaction.createPurchase(
                7L,
                new BigDecimal("600.00"),
                "Compra sem saldo",
                TransactionCategory.FOOD,
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("2000.00"),
                BigDecimal.ZERO
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());

        assertEquals("Saldo insuficiente", transaction.getRejectionReason());
    }

    @Test
    void shouldSaveRejectedPurchaseAboveTransactionLimit() {
        Transaction transaction = Transaction.createPurchase(
                7L,
                new BigDecimal("600.00"),
                "Compra acima do limite",
                TransactionCategory.SERVICES,
                new BigDecimal("2000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("2000.00"),
                BigDecimal.ZERO
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());

        assertEquals("Valor acima do limite por transação", transaction.getRejectionReason());
    }

    @Test
    void shouldSaveRejectedPurchaseAboveDailyLimit() {
        Transaction transaction = Transaction.createPurchase(
                7L,
                new BigDecimal("300.00"),
                "Compra acima do limite diário",
                TransactionCategory.TRANSPORT,
                new BigDecimal("2000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("800.00")
        );

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());

        assertEquals("Limite diário excedido", transaction.getRejectionReason());
    }
}
