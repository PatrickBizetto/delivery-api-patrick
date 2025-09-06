package com.delivery_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
            // Seção de Informações Gerais da API
            .info(new Info()
                .title("Delivery API")
                .version("1.0.0")
                .description("API REST completa para plataforma de delivery da FoodTech Brasil.")
                .contact(new Contact()
                        .name("Equipe Delivery API")
                        .email("dev@delivery_api.com")
                        .url("https://delivery_api.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")))
            
            // Seção de Servidores (Ambientes)
            .servers(List.of(
                    new Server()
                            .url("http://localhost:8080")
                            .description("Servidor de Desenvolvimento"),
                    new Server()
                            .url("https://api.delivery_api.com")
                            .description("Servidor de Produção")
            ))

            // Seção de Segurança JWT
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(
                new Components()
                    .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            );
    }
}