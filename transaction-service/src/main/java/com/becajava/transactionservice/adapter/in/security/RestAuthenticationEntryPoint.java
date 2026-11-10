package com.becajava.transactionservice.adapter.in.security;

import com.becajava.transactionservice.adapter.in.web.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JsonMapper jsonMapper;

    public RestAuthenticationEntryPoint(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"transaction-service\"");

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Autenticação necessária ou credenciais inválidas",
                Map.of()
        );

        jsonMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}