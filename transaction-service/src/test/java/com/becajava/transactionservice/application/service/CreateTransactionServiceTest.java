package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.model.CreateTransactionCommand;
import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionCategory;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.out.AccountBalancePort;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.becajava.transactionservice.application.exception.AccountServiceUnavailableException;
import com.becajava.transactionservice.application.model.AccountBalance;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
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
    private AccountBalancePort accountBalancePort;
    private CreateTransactionService createTransactionService;

    @BeforeEach
    void setUp() {
        transactionRepositoryPort = mock(TransactionRepositoryPort.class);

        accountBalancePort = mock(AccountBalancePort.class);

        createTransactionService =
                new CreateTransactionService(
                        transactionRepositoryPort,
                        accountBalancePort
                );
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

        verifyNoInteractions(accountBalancePort);
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
                        InvalidTransactionException.class, () -> createTransactionService.createTransaction(command)
                );

        assertEquals("Tipo de transação ainda não suportado", exception.getMessage());

        verifyNoInteractions(transactionRepositoryPort, accountBalancePort);
    }

    @Test
    void shouldCreateApprovedWithdrawal() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(
                        7L,
                        new BigDecimal("300.00"),
                        "Retirada aprovada",
                        TransactionCategory.OTHER,
                        TransactionType.WITHDRAWAL,
                        null
                );

        AccountBalance accountBalance =
                new AccountBalance(
                        7L,
                        new BigDecimal("1000.00"),
                        new BigDecimal("1000.00"),
                        new BigDecimal("500.00")
                );

        when(accountBalancePort.findByUserId(7L)).thenReturn(Optional.of(accountBalance));

        when(
                transactionRepositoryPort
                        .sumApprovedOutgoingAmountByOwnerUserIdBetween(
                                eq(7L),
                                any(LocalDateTime.class),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(new BigDecimal("200.00"));

        when(transactionRepositoryPort.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = createTransactionService.createTransaction(command);

        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertNull(result.getRejectionReason());

        verify(accountBalancePort).findByUserId(7L);

        verify(transactionRepositoryPort).save(any(Transaction.class));
    }

    @Test
    void shouldSaveRejectedWithdrawalWhenBalanceIsInsufficient() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(
                        7L,
                        new BigDecimal("600.00"),
                        "Retirada sem saldo",
                        TransactionCategory.OTHER,
                        TransactionType.WITHDRAWAL,
                        null
                );

        AccountBalance accountBalance =
                new AccountBalance(
                        7L,
                        new BigDecimal("500.00"),
                        new BigDecimal("2000.00"),
                        new BigDecimal("1000.00")
                );

        when(accountBalancePort.findByUserId(7L)).thenReturn(Optional.of(accountBalance));

        when(
                transactionRepositoryPort
                        .sumApprovedOutgoingAmountByOwnerUserIdBetween(
                                eq(7L),
                                any(LocalDateTime.class),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(BigDecimal.ZERO);

        when(transactionRepositoryPort.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = createTransactionService.createTransaction(command);

        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Saldo insuficiente", result.getRejectionReason());

        verify(transactionRepositoryPort).save(any(Transaction.class));
    }

    @Test
    void shouldNotSaveWhenAccountServiceIsUnavailable() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(
                        7L,
                        new BigDecimal("300.00"),
                        "Retirada",
                        TransactionCategory.OTHER,
                        TransactionType.WITHDRAWAL,
                        null
                );

        when(accountBalancePort.findByUserId(7L))
                .thenThrow(
                        new AccountServiceUnavailableException(
                                new RuntimeException("Falha simulada")
                        )
                );

        assertThrows(AccountServiceUnavailableException.class, () -> createTransactionService.createTransaction(command));

        verifyNoInteractions(transactionRepositoryPort);
    }
}