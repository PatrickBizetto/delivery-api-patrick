package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO para autenticação de usuário. Contém as credenciais de login.")
public class LoginRequest {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ter um formato válido.")
    @Schema(description = "Endereço de e-mail cadastrado do usuário.", 
            example = "cliente@example.com", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Schema(description = "Senha de acesso do usuário.", 
            example = "senha123", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String senha;
}