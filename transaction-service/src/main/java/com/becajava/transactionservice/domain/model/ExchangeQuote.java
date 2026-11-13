package com.becajava.transactionservice.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ExchangeQuote {

    private final String currency;
    private final BigDecimal rate;
    private final BigDecimal convertedAmount;
    private final LocalDate quoteDate;

    public ExchangeQuote(
            String currency,
            BigDecimal rate,
            BigDecimal convertedAmount,
            LocalDate quoteDate
    ) {
        this.currency = currency;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
        this.quoteDate = quoteDate;
    }

    public static ExchangeQuote fromBrl(
            String currency,
            BigDecimal amountInBrl,
            BigDecimal rate,
            LocalDate quoteDate
    ) {
        BigDecimal convertedAmount = amountInBrl
                .divide(rate, 2, RoundingMode.HALF_UP);

        return new ExchangeQuote(
                currency,
                rate,
                convertedAmount,
                quoteDate
        );
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