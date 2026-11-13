package com.becajava.transactionservice.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeQuoteResponse {

    private final String currency;
    private final BigDecimal rate;
    private final BigDecimal convertedAmount;
    private final LocalDate quoteDate;

    public ExchangeQuoteResponse(String currency, BigDecimal rate, BigDecimal convertedAmount, LocalDate quoteDate) {
        this.currency = currency;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
        this.quoteDate = quoteDate;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public LocalDate getQuoteDate() {
        return quoteDate;
    }
}