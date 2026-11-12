package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.exception.AccountBalanceNotFoundException;
import com.becajava.transactionservice.application.model.AccountBalance;
import com.becajava.transactionservice.port.out.AccountBalancePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetAccountBalanceServiceTest {

    private AccountBalancePort accountBalancePort;
    private GetAccountBalanceService getAccountBalanceService;

    @BeforeEach
    void setUp() {
        accountBalancePort = mock(AccountBalancePort.class);

        getAccountBalanceService = new GetAccountBalanceService(accountBalancePort);
    }

    @Test
    void shouldReturnAccountBalanceWhenAccountExists() {
        AccountBalance accountBalance =
                new AccountBalance(
                        1L,
                        new BigDecimal("5000.00"),
                        new BigDecimal("2000.00"),
                        new BigDecimal("1000.00")
                );

        when(accountBalancePort.findByUserId(1L)).thenReturn(Optional.of(accountBalance));

        AccountBalance result = getAccountBalanceService.getByUserId(1L);

        assertEquals(1L, result.getUserId());
        assertEquals(new BigDecimal("5000.00"), result.getBalance());
        assertEquals(new BigDecimal("2000.00"), result.getDailyLimit());
        assertEquals(new BigDecimal("1000.00"), result.getTransactionLimit());

        verify(accountBalancePort).findByUserId(1L);
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {
        when(accountBalancePort.findByUserId(999L)).thenReturn(Optional.empty());

        AccountBalanceNotFoundException exception =
                assertThrows(
                        AccountBalanceNotFoundException.class,
                        () -> getAccountBalanceService.getByUserId(999L)
                );

        assertEquals("Conta de saldo e limites não encontrada para o usuário: 999", exception.getMessage());

        verify(accountBalancePort).findByUserId(999L);
    }
}