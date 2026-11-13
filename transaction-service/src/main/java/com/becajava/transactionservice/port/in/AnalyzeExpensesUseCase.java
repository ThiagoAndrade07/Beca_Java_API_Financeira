package com.becajava.transactionservice.port.in;

import com.becajava.transactionservice.application.model.ExpenseAnalysisResult;
import com.becajava.transactionservice.application.model.ExpenseGrouping;

import java.time.LocalDate;

public interface AnalyzeExpensesUseCase {

    ExpenseAnalysisResult analyze(
            Long ownerUserId,
            ExpenseGrouping groupBy,
            LocalDate startDate,
            LocalDate endDate
    );
}