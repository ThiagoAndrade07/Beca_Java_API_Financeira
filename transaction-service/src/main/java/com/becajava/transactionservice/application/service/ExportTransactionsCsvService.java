package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.ExportTransactionsCsvUseCase;
import com.becajava.transactionservice.port.in.ListTransactionsUseCase;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportTransactionsCsvService
        implements ExportTransactionsCsvUseCase {

    private static final String SEPARATOR = ";";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

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

        appendHeader(csv);

        for (Transaction transaction : transactions) {
            appendTransaction(csv, transaction);
        }

        String csvWithBom = "\uFEFF" + csv;

        return csvWithBom.getBytes(StandardCharsets.UTF_8);
    }

    private void appendHeader(StringBuilder csv) {
        csv.append("id").append(SEPARATOR);
        csv.append("tipo").append(SEPARATOR);
        csv.append("categoria").append(SEPARATOR);
        csv.append("descricao").append(SEPARATOR);
        csv.append("valorEmReal").append(SEPARATOR);
        csv.append("dataHora").append(SEPARATOR);
        csv.append("status").append(SEPARATOR);
        csv.append("motivoRejeicao").append(SEPARATOR);
        csv.append("usuarioDestinoId");
        csv.append("\n");
    }

    private void appendTransaction(
            StringBuilder csv,
            Transaction transaction
    ) {
        csv.append(transaction.getId()).append(SEPARATOR);

        csv.append(transaction.getType()).append(SEPARATOR);

        csv.append(transaction.getCategory()).append(SEPARATOR);

        csv.append(
                csvValue(transaction.getDescription())
        ).append(SEPARATOR);

        csv.append(
                formatAmount(transaction.getAmount())
        ).append(SEPARATOR);

        csv.append(
                transaction
                        .getOccurredAt()
                        .format(DATE_TIME_FORMATTER)
        ).append(SEPARATOR);

        csv.append(transaction.getStatus()).append(SEPARATOR);

        csv.append(
                csvValue(transaction.getRejectionReason())
        ).append(SEPARATOR);

        csv.append(
                transaction.getDestinationUserId() == null
                        ? ""
                        : transaction.getDestinationUserId()
        );

        csv.append("\n");
    }

    private String formatAmount(BigDecimal amount) {
        return amount
                .toPlainString()
                .replace(".", ",");
    }

    private String csvValue(String value) {
        if (value == null) {
            return "";
        }

        String escapedValue =
                value.replace("\"", "\"\"");

        return "\"" + escapedValue + "\"";
    }
}