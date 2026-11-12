package com.becajava.transactionservice.adapter.in.web.mapper;

import com.becajava.transactionservice.adapter.in.web.dto.ExpenseSummaryResponse;
import com.becajava.transactionservice.application.model.ExpenseSummary;

public final class ExpenseAnalysisWebMapper {

    private ExpenseAnalysisWebMapper() {
    }

    public static ExpenseSummaryResponse toResponse(
            ExpenseSummary summary
    ) {
        return new ExpenseSummaryResponse(
                summary.getGroup(),
                summary.getTotal()
        );
    }
}