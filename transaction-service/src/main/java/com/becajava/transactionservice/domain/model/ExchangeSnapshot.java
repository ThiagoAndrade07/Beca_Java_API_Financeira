package com.becajava.transactionservice.domain.model;

public class ExchangeSnapshot {

    private final boolean available;
    private final ExchangeQuote usd;
    private final ExchangeQuote eur;
    private final String unavailableReason;

    private ExchangeSnapshot(boolean available, ExchangeQuote usd, ExchangeQuote eur, String unavailableReason) {
        this.available = available;
        this.usd = usd;
        this.eur = eur;
        this.unavailableReason = unavailableReason;
    }

    public static ExchangeSnapshot available(ExchangeQuote usd, ExchangeQuote eur) {
        return new ExchangeSnapshot(
                true,
                usd,
                eur,
                null
        );
    }

    public static ExchangeSnapshot unavailable(String reason) {
        return new ExchangeSnapshot(
                false,
                null,
                null,
                reason
        );
    }

    public boolean isAvailable() {
        return available;
    }

    public ExchangeQuote getUsd() {
        return usd;
    }

    public ExchangeQuote getEur() {
        return eur;
    }

    public String getUnavailableReason() {
        return unavailableReason;
    }
}