package com.becajava.transactionservice.adapter.out.client;

import com.becajava.transactionservice.application.exception.AccountServiceUnavailableException;
import com.becajava.transactionservice.application.model.AccountBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountBalanceRestAdapterTest {

    private MockRestServiceServer mockServer;
    private AccountBalanceRestAdapter accountBalanceRestAdapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder();

        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();

        accountBalanceRestAdapter =
                new AccountBalanceRestAdapter(
                        restClientBuilder,
                        "http://mock-api"
                );
    }

    @Test
    void shouldFindAccountBalanceByUserId() {
        String responseBody = """
                [
                  {
                    "id": "1",
                    "userId": 7,
                    "balance": 5000.00,
                    "dailyLimit": 2000.00,
                    "transactionLimit": 1000.00
                  }
                ]
                """;

        mockServer.expect(
                        once(),
                        requestTo("http://mock-api/accounts?userId=7")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess(
                                responseBody,
                                MediaType.APPLICATION_JSON
                        )
                );

        AccountBalance accountBalance = accountBalanceRestAdapter.findByUserId(7L).orElseThrow();

        assertEquals(7L, accountBalance.getUserId());

        assertEquals(new BigDecimal("5000.00"), accountBalance.getBalance());

        assertEquals(new BigDecimal("2000.00"), accountBalance.getDailyLimit());

        assertEquals(new BigDecimal("1000.00"), accountBalance.getTransactionLimit());

        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        mockServer.expect(
                        once(),
                        requestTo("http://mock-api/accounts?userId=999")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess(
                                "[]",
                                MediaType.APPLICATION_JSON
                        )
                );

        var result =
                accountBalanceRestAdapter.findByUserId(999L);

        assertTrue(result.isEmpty());

        mockServer.verify();
    }

    @Test
    void shouldTranslateMockApiFailureToAccountServiceUnavailableException() {
        mockServer.expect(
                        once(),
                        requestTo("http://mock-api/accounts?userId=7")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThrows(AccountServiceUnavailableException.class, () -> accountBalanceRestAdapter.findByUserId(7L));

        mockServer.verify();
    }
}