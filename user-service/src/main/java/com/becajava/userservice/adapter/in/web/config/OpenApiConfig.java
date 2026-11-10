package com.becajava.userservice.adapter.in.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BASIC_AUTH =
            "basicAuth";

    @Bean
    public OpenAPI userServiceOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        "Beca Java 2026 - User Service API"
                                )
                                .description(
                                        "API para cadastro, consulta, atualização, "
                                                + "exclusão e importação de usuários."
                                )
                                .version("v1")
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        BASIC_AUTH,
                                        new SecurityScheme()
                                                .type(
                                                        SecurityScheme.Type.HTTP
                                                )
                                                .scheme("basic")
                                )
                );
    }
}