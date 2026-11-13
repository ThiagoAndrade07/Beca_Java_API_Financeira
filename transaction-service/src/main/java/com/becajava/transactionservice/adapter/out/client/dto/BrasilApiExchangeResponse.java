package com.becajava.transactionservice.adapter.out.client.dto;

import java.time.LocalDate;
import java.util.List;

public class BrasilApiExchangeResponse {

    private List<BrasilApiQuoteResponse> cotacoes;
    private String moeda;
    private LocalDate data;

    public BrasilApiExchangeResponse() {
    }

    public List<BrasilApiQuoteResponse> getCotacoes() {
        return cotacoes;
    }

    public void setCotacoes(List<BrasilApiQuoteResponse> cotacoes) {
        this.cotacoes = cotacoes;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}