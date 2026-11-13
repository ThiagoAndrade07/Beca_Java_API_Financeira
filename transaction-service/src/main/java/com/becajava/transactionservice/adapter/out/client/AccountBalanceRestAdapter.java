package com.becajava.transactionservice.adapter.out.client;

import com.becajava.transactionservice.adapter.out.client.dto.AccountBalanceApiResponse;
import com.becajava.transactionservice.application.exception.AccountBalanceNotFoundException;
import com.becajava.transactionservice.application.exception.AccountServiceUnavailableException;
import com.becajava.transactionservice.application.model.AccountBalance;
import com.becajava.transactionservice.port.out.AccountBalancePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Component
public class AccountBalanceRestAdapter implements AccountBalancePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountBalanceRestAdapter.class);

    private final RestClient restClient;

    public AccountBalanceRestAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${clients.account-api.base-url}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<AccountBalance> findByUserId(Long userId) {
        return findApiAccountByUserId(userId).map(this::toAccountBalance);
    }

    @Override
    public void updateBalance(Long userId, BigDecimal newBalance) {
        AccountBalanceApiResponse apiAccount =
                findApiAccountByUserId(userId)
                        .orElseThrow(
                                () -> new AccountBalanceNotFoundException(userId)
                        );

        try {
            restClient
                    .put()
                    .uri("/accounts/{id}", apiAccount.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            Map.of(
                                    "balance",
                                    newBalance
                            )
                    )
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound exception) {
            throw new AccountBalanceNotFoundException(userId);

        } catch (RestClientException exception) {
            LOGGER.error("Erro ao atualizar o saldo do usuário {}", userId, exception);

            throw new AccountServiceUnavailableException(exception);
        }
    }

    private Optional<AccountBalanceApiResponse> findApiAccountByUserId(Long userId) {
        try {
            AccountBalanceApiResponse[] accounts =
                    restClient
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/accounts")
                                    .queryParam("userId", userId)
                                    .build()
                            )
                            .retrieve()
                            .body(AccountBalanceApiResponse[].class);

            if (accounts == null || accounts.length == 0) {
                return Optional.empty();
            }

            return Optional.of(accounts[0]);

        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();

        } catch (RestClientException exception) {
            LOGGER.error("Erro ao consultar saldo e limites do usuário {}", userId, exception);

            throw new AccountServiceUnavailableException(exception);
        }
    }

    private AccountBalance toAccountBalance(AccountBalanceApiResponse apiAccount) {
        return new AccountBalance(
                apiAccount.getUserId(),
                apiAccount.getBalance(),
                apiAccount.getDailyLimit(),
                apiAccount.getTransactionLimit()
        );
    }
}