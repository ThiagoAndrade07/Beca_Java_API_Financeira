package com.becajava.transactionservice.adapter.in.web.mapper;

import com.becajava.transactionservice.adapter.in.web.dto.ExchangeQuoteResponse;
import com.becajava.transactionservice.adapter.in.web.dto.ExchangeSnapshotResponse;
import com.becajava.transactionservice.domain.model.ExchangeQuote;
import com.becajava.transactionservice.domain.model.ExchangeSnapshot;

public final class ExchangeSnapshotWebMapper {

    private ExchangeSnapshotWebMapper() {
    }

    public static ExchangeSnapshotResponse toResponse(ExchangeSnapshot snapshot) {
        if (snapshot == null) {
            return new ExchangeSnapshotResponse(
                    false,
                    null,
                    null,
                    "Conversão indisponível"
            );
        }

        if (!snapshot.isAvailable()) {
            return new ExchangeSnapshotResponse(
                    false,
                    null,
                    null,
                    snapshot.getUnavailableReason()
            );
        }

        return new ExchangeSnapshotResponse(
                true,
                toQuoteResponse(snapshot.getUsd()),
                toQuoteResponse(snapshot.getEur()),
                null
        );
    }

    private static ExchangeQuoteResponse toQuoteResponse(ExchangeQuote quote) {
        return new ExchangeQuoteResponse(
                quote.getCurrency(),
                quote.getRate(),
                quote.getConvertedAmount(),
                quote.getQuoteDate()
        );
    }
}
