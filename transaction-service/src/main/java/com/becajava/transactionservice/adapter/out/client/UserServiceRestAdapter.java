package com.becajava.transactionservice.adapter.out.client;

import com.becajava.transactionservice.application.exception.UserServiceUnavailableException;
import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.port.out.UserServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UserServiceRestAdapter implements UserServicePort {

    private final RestClient restClient;

    public UserServiceRestAdapter(RestClient.Builder restClientBuilder, @Value("${clients.user-service.base-url}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser(String authorizationHeader) {
        try {
            return restClient
                    .get()
                    .uri("/users/me")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            authorizationHeader
                    )
                    .retrieve()
                    .body(AuthenticatedUser.class);

        } catch (HttpClientErrorException.Unauthorized exception) {
            throw exception;

        } catch (RestClientException exception) {
            throw new UserServiceUnavailableException(exception);
        }
    }

}
