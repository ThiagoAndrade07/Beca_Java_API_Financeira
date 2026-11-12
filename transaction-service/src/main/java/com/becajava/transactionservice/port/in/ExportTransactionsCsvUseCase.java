package com.becajava.transactionservice.port.in;

import com.becajava.transactionservice.domain.model.TransactionType;

import java.time.LocalDate;

public interface ExportTransactionsCsvUseCase {

    byte[] export(Long ownerUserId, TransactionType type, LocalDate startDate, LocalDate endDate);
}