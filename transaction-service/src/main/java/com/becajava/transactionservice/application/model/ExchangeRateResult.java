package com.becajava.transactionservice.application.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeRateResult {

    private final String currency;
    private final BigDecimal sellingRate;
    private final LocalDate quoteDate;

    public ExchangeRateResult(
            String currency,
            BigDecimal sellingRate,
            LocalDate quoteDate
    ) {
        this.currency = currency;
        this.sellingRate = sellingRate;
        this.quoteDate = quoteDate;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getSellingRate() {
        return sellingRate;
    }

    public LocalDate getQuoteDate() {
        return quoteDate;
    }
}