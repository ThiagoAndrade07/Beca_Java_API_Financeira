package com.becajava.transactionservice.application.model;

import java.math.BigDecimal;

public class ExpenseSummary {

    private final String group;
    private final BigDecimal total;

    public ExpenseSummary(String group, BigDecimal total) {
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