package com.becajava.transactionservice.adapter.in.security;

import com.becajava.transactionservice.application.exception.UserServiceUnavailableException;
import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.port.out.UserServicePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = SecurityConfigTest.TestController.class)
@Import({
        SecurityConfig.class,
        UserServiceAuthenticationFilter.class,
        RestAuthenticationEntryPoint.class,
        SecurityConfigTest.TestController.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServicePort userServicePort;

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreMissing() throws Exception {
        mockMvc.perform(get("/test/authenticated"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Autenticação necessária ou credenciais inválidas"));
    }

    @Test
    void shouldReturnUnauthorizedWhenUserServiceRejectsCredentials() throws Exception {
        String authorizationHeader = "Basic dXNlcjpzZW5oYS1lcnJhZGE=";

        HttpClientErrorException unauthorizedException =
                HttpClientErrorException.create(
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        HttpHeaders.EMPTY,
                        new byte[0],
                        null
                );

        when(userServicePort.getAuthenticatedUser(authorizationHeader)).thenThrow(unauthorizedException);

        mockMvc.perform(
                        get("/test/authenticated").header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Autenticação necessária ou credenciais inválidas"));
    }

    @Test
    void shouldAllowRequestWhenUserServiceAcceptsCredentials() throws Exception {
        String authorizationHeader = "Basic dXNlcjpzZW5oYS1jb3JyZXRh";

        AuthenticatedUser authenticatedUser =
                new AuthenticatedUser(
                        7L,
                        "Usuário Teste",
                        "usuario@example.com"
                );

        when(userServicePort.getAuthenticatedUser(authorizationHeader))
                .thenReturn(authenticatedUser);

        mockMvc.perform(
                        get("/test/authenticated")
                                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("autenticado"));
    }

    @Test
    void shouldReturnServiceUnavailableWhenUserServiceIsUnavailable() throws Exception {
        String authorizationHeader = "Basic dXNlcjpzZW5oYQ==";

        when(userServicePort.getAuthenticatedUser(authorizationHeader))
                .thenThrow(new UserServiceUnavailableException(new RuntimeException("Falha simulada de conexão")));

        mockMvc.perform(get("/test/authenticated").header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message").value("Serviço de usuários temporariamente indisponível"));
    }

    @RestController
    public static class TestController {

        @GetMapping("/test/authenticated")
        public String authenticated() {
            return "autenticado";
        }
    }
}