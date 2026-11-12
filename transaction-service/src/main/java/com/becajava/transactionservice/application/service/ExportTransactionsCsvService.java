package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.ExportTransactionsCsvUseCase;
import com.becajava.transactionservice.port.in.ListTransactionsUseCase;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExportTransactionsCsvService
        implements ExportTransactionsCsvUseCase {

    private final ListTransactionsUseCase listTransactionsUseCase;

    public ExportTransactionsCsvService(
            ListTransactionsUseCase listTransactionsUseCase
    ) {
        this.listTransactionsUseCase = listTransactionsUseCase;
    }

    @Override
    public byte[] export(
            Long ownerUserId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Transaction> transactions =
                listTransactionsUseCase.listByOwnerUserId(
                        ownerUserId,
                        type,
                        startDate,
                        endDate
                );

        StringBuilder csv = new StringBuilder();

        csv.append(
                "id,amount,occurredAt,description,type,category,"
                        + "status,rejectionReason,destinationUserId\n"
        );

        for (Transaction transaction : transactions) {
            csv.append(transaction.getId()).append(",");
            csv.append(transaction.getAmount()).append(",");
            csv.append(transaction.getOccurredAt()).append(",");
            csv.append(csvValue(transaction.getDescription())).append(",");
            csv.append(transaction.getType()).append(",");
            csv.append(transaction.getCategory()).append(",");
            csv.append(transaction.getStatus()).append(",");
            csv.append(csvValue(transaction.getRejectionReason())).append(",");
            csv.append(
                    transaction.getDestinationUserId() == null
                            ? ""
                            : transaction.getDestinationUserId()
            );
            csv.append("\n");
        }

        String csvWithBom = "\uFEFF" + csv;

        return csvWithBom.getBytes(StandardCharsets.UTF_8);
    }

    private String csvValue(String value) {
        if (value == null) {
            return "";
        }

        String escapedValue = value.replace("\"", "\"\"");

        return "\"" + escapedValue + "\"";
    }
}