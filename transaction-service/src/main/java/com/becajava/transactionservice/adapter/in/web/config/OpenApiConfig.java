package com.becajava.transactionservice.adapter.in.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BASIC_AUTH = "basicAuth";

    @Bean
    public OpenAPI transactionServiceOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        "Beca Java 2026 - Transaction Service API"
                                )
                                .description(
                                        "API para criação, consulta e análise "
                                                + "de transações financeiras."
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
                ).addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH));
    }
}