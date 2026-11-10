package com.becajava.transactionservice.adapter.out.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        Duration timeout = Duration.ofSeconds(3);

        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(timeout);

        return RestClient.builder().requestFactory(requestFactory);
    }
}