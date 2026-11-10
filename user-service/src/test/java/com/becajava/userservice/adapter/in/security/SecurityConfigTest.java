package com.becajava.userservice.adapter.in.security;

import com.becajava.userservice.adapter.in.web.controller.UserController;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.in.CreateUserUseCase;
import com.becajava.userservice.port.in.DeleteUserUseCase;
import com.becajava.userservice.port.in.GetUserByIdUseCase;
import com.becajava.userservice.port.in.ListUsersUseCase;
import com.becajava.userservice.port.in.UpdateUserUseCase;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import com.becajava.userservice.port.in.GetAuthenticatedUserUseCase;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ImportAutoConfiguration(
        ServletWebSecurityAutoConfiguration.class
)
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        DatabaseUserDetailsService.class
})
class SecurityConfigTest {

    private static final String EMAIL = "maria@example.com";

    private static final String PASSWORD = "Senha123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserRepositoryPort userRepositoryPort;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private ListUsersUseCase listUsersUseCase;

    @MockitoBean
    private GetUserByIdUseCase getUserByIdUseCase;

    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;

    @MockitoBean
    private DeleteUserUseCase deleteUserUseCase;

    @MockitoBean
    private GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;

    @BeforeEach
    void setUpAuthenticatedUser() {
        User user = new User(
                1L,
                "Maria Silva",
                EMAIL,
                passwordEncoder.encode(PASSWORD)
        );

        when(getAuthenticatedUserUseCase.getAuthenticatedUser(EMAIL)).thenReturn(user);

        when(userRepositoryPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        when(listUsersUseCase.listUsers()).thenReturn(List.of());
    }

    @Test
    void shouldReturnUnauthorizedWithoutCredentials() throws Exception {

        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Autenticação necessária ou credenciais inválidas"));
    }

    @Test
    void shouldReturnUnauthorizedWithWrongPassword() throws Exception {

        mockMvc.perform(get("/users")
                        .with(httpBasic(
                                EMAIL,
                                "SenhaIncorreta"
                        ))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Autenticação necessária ou credenciais inválidas"));
    }

    @Test
    void shouldReturnOkWithValidCredentials() throws Exception {

        mockMvc.perform(
                        get("/users")
                                .with(httpBasic(
                                        EMAIL,
                                        PASSWORD
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldAllowUserCreationWithoutAuthentication() throws Exception {

        User createdUser = new User(
                2L,
                "João Souza",
                "joao@example.com",
                "password-hash"
        );

        when(createUserUseCase.createUser(
                "João Souza",
                "joao@example.com",
                "OutraSenha456"
        )).thenReturn(createdUser);

        String requestBody = """
                {
                  "name": "João Souza",
                  "email": "joao@example.com",
                  "password": "OutraSenha456"
                }
                """;

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("João Souza"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    void shouldReturnAuthenticatedUser() throws Exception {

        mockMvc.perform(
                        get("/users/me")
                                .with(httpBasic(
                                        EMAIL,
                                        PASSWORD
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }
}