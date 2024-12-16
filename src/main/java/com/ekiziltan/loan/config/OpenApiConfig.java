package com.ekiziltan.loan.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization") // Header ismi
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description("JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"");


        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization", List.of("read", "write"));

        return new OpenAPI()
                .info(new Info().title("Loan API").version("1.0").description("Loan Management API with JWT Security"))
                .addSecurityItem(securityRequirement)
                .components(new Components().addSecuritySchemes("Authorization", securityScheme));
    }
}
