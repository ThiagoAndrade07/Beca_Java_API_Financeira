package com.becajava.transactionservice.adapter.in.security;

import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.port.out.UserServicePort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class UserServiceAuthenticationFilter extends OncePerRequestFilter {

    private final UserServicePort userServicePort;

    public UserServiceAuthenticationFilter(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
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
            }
        }

        filterChain.doFilter(request, response);
    }
}