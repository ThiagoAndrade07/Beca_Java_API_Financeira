package com.becajava.transactionservice.port.out;

import com.becajava.transactionservice.application.model.ExchangeRateResult;

import java.time.LocalDate;

public interface ExchangeRatePort {

    ExchangeRateResult getSellingRate(String currency, LocalDate date);
}