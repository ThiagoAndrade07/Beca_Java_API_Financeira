package com.becajava.transactionservice.adapter.in.security;

import com.becajava.transactionservice.adapter.in.web.dto.ApiErrorResponse;
import com.becajava.transactionservice.application.exception.UserServiceUnavailableException;
import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.port.out.UserServicePort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class UserServiceAuthenticationFilter extends OncePerRequestFilter {

    private final UserServicePort userServicePort;
    private final JsonMapper jsonMapper;

    public UserServiceAuthenticationFilter(UserServicePort userServicePort, JsonMapper jsonMapper) {
        this.userServicePort = userServicePort;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        boolean hasBasicCredentials = authorizationHeader != null && authorizationHeader.startsWith("Basic ");

        boolean isNotAuthenticated = SecurityContextHolder.getContext().getAuthentication() == null;

        if (hasBasicCredentials && isNotAuthenticated) {
            try {
                AuthenticatedUser authenticatedUser = userServicePort.getAuthenticatedUser(authorizationHeader);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                authenticatedUser,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_USER"
                                        )
                                )
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (HttpClientErrorException.Unauthorized exception) {
                SecurityContextHolder.clearContext();

            } catch (UserServiceUnavailableException exception) {
                SecurityContextHolder.clearContext();

                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                ApiErrorResponse errorResponse = new ApiErrorResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Serviço de usuários temporariamente indisponível",
                        Map.of()
                );

                jsonMapper.writeValue(
                        response.getOutputStream(),
                        errorResponse
                );

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}