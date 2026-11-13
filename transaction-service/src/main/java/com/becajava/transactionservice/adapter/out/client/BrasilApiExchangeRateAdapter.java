package com.becajava.transactionservice.adapter.out.client;

import com.becajava.transactionservice.adapter.out.client.dto.BrasilApiExchangeResponse;
import com.becajava.transactionservice.adapter.out.client.dto.BrasilApiQuoteResponse;
import com.becajava.transactionservice.application.exception.ExchangeRateNotFoundException;
import com.becajava.transactionservice.application.exception.ExchangeServiceUnavailableException;
import com.becajava.transactionservice.application.model.ExchangeRateResult;
import com.becajava.transactionservice.port.out.ExchangeRatePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class BrasilApiExchangeRateAdapter
        implements ExchangeRatePort {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    BrasilApiExchangeRateAdapter.class
            );

    private final RestClient restClient;

    public BrasilApiExchangeRateAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${clients.brasil-api.base-url}")
            String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public ExchangeRateResult getSellingRate(
            String currency,
            LocalDate date
    ) {
        try {
            BrasilApiExchangeResponse response = restClient
                    .get()
                    .uri(
                            "/cambio/v1/cotacao/{currency}/{date}",
                            currency,
                            date
                    )
                    .retrieve()
                    .body(BrasilApiExchangeResponse.class);

            return extractLatestExchangeRate(
                    response,
                    currency
            );

        } catch (
                HttpClientErrorException.BadRequest exception
        ) {
            String responseBody =
                    exception.getResponseBodyAsString();

            if (
                    responseBody != null
                            && responseBody.contains(
                            "NO_TODAY_DATE"
                    )
            ) {
                LocalDate previousDate =
                        date.minusDays(1);

                LOGGER.info(
                        "BrasilAPI não permite cotação "
                                + "do dia atual. "
                                + "Consultando a data anterior: {}",
                        previousDate
                );

                return getSellingRate(
                        currency,
                        previousDate
                );
            }

            throw new ExchangeRateNotFoundException(
                    currency
            );

        } catch (
                HttpClientErrorException.NotFound exception
        ) {
            throw new ExchangeRateNotFoundException(
                    currency
            );

        } catch (RestClientException exception) {
            LOGGER.error(
                    "Erro ao consultar cotação "
                            + "da moeda {} na data {}",
                    currency,
                    date,
                    exception
            );

            throw new ExchangeServiceUnavailableException(
                    exception
            );
        }
    }

    private ExchangeRateResult extractLatestExchangeRate(
            BrasilApiExchangeResponse response,
            String currency
    ) {
        if (
                response == null
                        || response.getCotacoes() == null
                        || response.getData() == null
        ) {
            throw new ExchangeRateNotFoundException(
                    currency
            );
        }

        List<BrasilApiQuoteResponse> quotes =
                response.getCotacoes();

        BigDecimal sellingRate = quotes.stream()
                .map(
                        BrasilApiQuoteResponse::getSellingRate
                )
                .filter(
                        rate ->
                                rate != null
                                        && rate.signum() > 0
                )
                .reduce(
                        (previous, current) -> current
                )
                .orElseThrow(
                        () ->
                                new ExchangeRateNotFoundException(
                                        currency
                                )
                );

        return new ExchangeRateResult(
                currency,
                sellingRate,
                response.getData()
        );
    }
}