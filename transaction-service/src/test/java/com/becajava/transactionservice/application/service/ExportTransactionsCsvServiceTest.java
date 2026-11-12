package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionCategory;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.ListTransactionsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExportTransactionsCsvServiceTest {

    private ListTransactionsUseCase listTransactionsUseCase;
    private ExportTransactionsCsvService exportTransactionsCsvService;

    @BeforeEach
    void setUp() {
        listTransactionsUseCase =
                mock(ListTransactionsUseCase.class);

        exportTransactionsCsvService =
                new ExportTransactionsCsvService(
                        listTransactionsUseCase
                );
    }

    @Test
    void shouldExportTransactionsAsCsv() {
        Transaction transaction =
                new Transaction(
                        10L,
                        1L,
                        null,
                        new BigDecimal("50.00"),
                        LocalDateTime.of(
                                2026,
                                11,
                                12,
                                10,
                                30
                        ),
                        "Mercado, \"bairro\"",
                        TransactionCategory.FOOD,
                        TransactionType.PURCHASE,
                        TransactionStatus.APPROVED,
                        null,
                        null
                );

        when(
                listTransactionsUseCase.listByOwnerUserId(
                        1L,
                        null,
                        null,
                        null
                )
        ).thenReturn(List.of(transaction));

        byte[] csvBytes =
                exportTransactionsCsvService.export(
                        1L,
                        null,
                        null,
                        null
                );

        String csv =
                new String(
                        csvBytes,
                        StandardCharsets.UTF_8
                );

        assertTrue(csv.startsWith("\uFEFF"));

        assertTrue(
                csv.contains(
                        "id,amount,occurredAt,description,type,"
                                + "category,status,rejectionReason,"
                                + "destinationUserId"
                )
        );

        assertTrue(
                csv.contains(
                        "\"Mercado, \"\"bairro\"\"\""
                )
        );

        assertTrue(csv.contains("PURCHASE"));
        assertTrue(csv.contains("FOOD"));
        assertTrue(csv.contains("APPROVED"));

        verify(listTransactionsUseCase)
                .listByOwnerUserId(
                        1L,
                        null,
                        null,
                        null
                );
    }
}