package com.becajava.userservice.adapter.in.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.becajava.userservice.adapter.in.web.dto.ApiErrorResponse;

import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

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

        response.setHeader(
                HttpHeaders.WWW_AUTHENTICATE,
                "Basic realm=\"user-service\""
        );

        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                "Autenticação necessária ou credenciais inválidas", Map.of()
        );

        jsonMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}