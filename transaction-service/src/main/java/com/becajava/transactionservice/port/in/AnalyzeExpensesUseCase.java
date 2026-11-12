package com.becajava.transactionservice.port.in;

import com.becajava.transactionservice.application.model.ExpenseGrouping;
import com.becajava.transactionservice.application.model.ExpenseSummary;

import java.time.LocalDate;
import java.util.List;

public interface AnalyzeExpensesUseCase {

    List<ExpenseSummary> analyze(
            Long ownerUserId,
            ExpenseGrouping groupBy,
            LocalDate startDate,
            LocalDate endDate
    );
}