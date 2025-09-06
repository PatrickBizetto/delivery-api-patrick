package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO que representa a resposta de uma autenticação bem-sucedida, contendo o token de acesso e os dados do usuário.")
public class LoginResponse {

    @Schema(description = "Token de acesso JWT gerado para o usuário autenticado.", 
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQGV4YW1wbGUuY29tIiwiaWF0IjoxNzI1NTc4NjAwLCJleHAiOjE3MjU2NjUwMDB9.5y_J-v6z...")
    private String token;

    @Schema(description = "Tipo do token de autenticação. O padrão é 'Bearer'.", 
            example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Dados completos do usuário que realizou o login.")
    private UserResponse usuario;
}