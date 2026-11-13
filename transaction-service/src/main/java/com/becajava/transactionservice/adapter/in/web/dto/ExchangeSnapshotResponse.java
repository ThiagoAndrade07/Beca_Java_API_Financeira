package com.becajava.transactionservice.adapter.in.web.dto;

public class ExchangeSnapshotResponse {

    private final boolean available;
    private final ExchangeQuoteResponse usd;
    private final ExchangeQuoteResponse eur;
    private final String unavailableReason;

    public ExchangeSnapshotResponse(boolean available, ExchangeQuoteResponse usd, ExchangeQuoteResponse eur, String unavailableReason) {
        this.available = available;
        this.usd = usd;
        this.eur = eur;
        this.unavailableReason = unavailableReason;
    }

    public boolean isAvailable() {
        return available;
    }

    public ExchangeQuoteResponse getUsd() {
        return usd;
    }

    public ExchangeQuoteResponse getEur() {
        return eur;
    }

    public String getUnavailableReason() {
        return unavailableReason;
    }
}