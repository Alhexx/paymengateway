package com.challenge.paymengateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;


@Configuration
public class SwaggerConfig {
    final String securitySchemeName = "bearerAuth";

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                            .name(securitySchemeName)
                            .type(Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .in(In.HEADER)
                            .description("Token no header: **Bearer {seu_token}**")))
                .info(new Info()
                        .title("Payment Gateway")
                        .version("v1.0")
                        .description("Documentação da API")
                        .license(new License()
                            .name("Apache 2.0")
                            .url("http://springdoc.org")));
    }
}
