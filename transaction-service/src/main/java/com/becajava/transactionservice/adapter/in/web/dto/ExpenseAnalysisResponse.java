package com.becajava.transactionservice.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseAnalysisResponse {

    private final List<ExpenseSummaryResponse> summaries;
    private final BigDecimal total;

    public ExpenseAnalysisResponse(
            List<ExpenseSummaryResponse> summaries,
            BigDecimal total
    ) {
        this.summaries = summaries;
        this.total = total;
    }

    public List<ExpenseSummaryResponse> getSummaries() {
        return summaries;
    }

    public BigDecimal getTotal() {
        return total;
    }
}