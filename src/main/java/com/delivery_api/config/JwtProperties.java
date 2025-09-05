package com.delivery_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "jwt") // Diz ao Spring para buscar propriedades que começam com "jwt"
@Data // Lombok para gerar getters e setters
public class JwtProperties {

    /**
     * Chave secreta para assinar e validar os tokens JWT.
     * Deve ser uma string longa e segura, codificada em Base64.
     */
    private String secret;

    /**
     * Tempo de expiração do token em milissegundos.
     * Padrão: 86400000 (24 horas).
     */
    private Long expiration;
}