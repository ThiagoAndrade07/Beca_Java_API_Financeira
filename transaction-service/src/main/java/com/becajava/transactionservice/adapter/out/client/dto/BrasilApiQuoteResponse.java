package com.becajava.transactionservice.adapter.out.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BrasilApiQuoteResponse {

    @JsonProperty("cotacao_venda")
    private BigDecimal sellingRate;

    @JsonProperty("tipo_boletim")
    private String bulletinType;

    public BrasilApiQuoteResponse() {
    }

    public BigDecimal getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(BigDecimal sellingRate) {
        this.sellingRate = sellingRate;
    }

    public String getBulletinType() {
        return bulletinType;
    }

    public void setBulletinType(String bulletinType) {
        this.bulletinType = bulletinType;
    }
}