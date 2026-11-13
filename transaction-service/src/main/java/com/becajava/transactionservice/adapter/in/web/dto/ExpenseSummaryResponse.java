package com.becajava.transactionservice.adapter.in.web.dto;

import java.math.BigDecimal;

public class ExpenseSummaryResponse {

    private final String group;
    private final BigDecimal total;

    public ExpenseSummaryResponse(
            String group,
            BigDecimal total
    ) {
        this.group = group;
        this.total = total;
    }

    public String getGroup() {
        return group;
    }

    public BigDecimal getTotal() {
        return total;
    }
}