package com.becajava.transactionservice.application.model;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseAnalysisResult {

    private final List<ExpenseSummary> summaries;
    private final BigDecimal total;

    public ExpenseAnalysisResult(
            List<ExpenseSummary> summaries,
            BigDecimal total
    ) {
        this.summaries = summaries;
        this.total = total;
    }

    public List<ExpenseSummary> getSummaries() {
        return summaries;
    }

    public BigDecimal getTotal() {
        return total;
    }
}