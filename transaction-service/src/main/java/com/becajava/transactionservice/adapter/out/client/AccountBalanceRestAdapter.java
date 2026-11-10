package com.becajava.transactionservice.adapter.out.client;

import com.becajava.transactionservice.application.model.AccountBalance;
import com.becajava.transactionservice.port.out.AccountBalancePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.becajava.transactionservice.application.exception.AccountServiceUnavailableException;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class AccountBalanceRestAdapter implements AccountBalancePort {

    private final RestClient restClient;

    public AccountBalanceRestAdapter(RestClient.Builder restClientBuilder, @Value("${clients.account-api.base-url}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public Optional<AccountBalance> findByUserId(Long userId) {
        try {
            AccountBalance[] accounts = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/accounts")
                            .queryParam("userId", userId)
                            .build()
                    )
                    .retrieve()
                    .body(AccountBalance[].class);

            if (accounts == null || accounts.length == 0) {
                return Optional.empty();
            }

            return Optional.of(accounts[0]);

        } catch (RestClientException exception) {
            throw new AccountServiceUnavailableException(exception);
        }
    }



}
