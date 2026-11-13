package com.becajava.transactionservice.adapter.in.web.mapper;

import com.becajava.transactionservice.adapter.in.web.dto.ExpenseAnalysisResponse;
import com.becajava.transactionservice.adapter.in.web.dto.ExpenseSummaryResponse;
import com.becajava.transactionservice.application.model.ExpenseAnalysisResult;
import com.becajava.transactionservice.application.model.ExpenseSummary;

import java.util.ArrayList;
import java.util.List;

public final class ExpenseAnalysisWebMapper {

    private ExpenseAnalysisWebMapper() {
    }

    public static ExpenseAnalysisResponse toResponse(
            ExpenseAnalysisResult result
    ) {
        List<ExpenseSummaryResponse> summaries = new ArrayList<>();

        for (ExpenseSummary summary : result.getSummaries()) {
            summaries.add(
                    new ExpenseSummaryResponse(
                            summary.getGroup(),
                            summary.getTotal()
                    )
            );
        }

        return new ExpenseAnalysisResponse(
                summaries,
                result.getTotal()
        );
    }
}