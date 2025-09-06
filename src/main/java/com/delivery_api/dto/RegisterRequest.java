package com.delivery_api.dto;

import com.delivery_api.enums.UserRole; // Import adicionado para clareza
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para registrar um novo usuário no sistema.")
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres.")
    @Schema(
        description = "Nome completo do usuário.",
        example = "João da Silva",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ter um formato válido.")
    @Schema(
        description = "E-mail do usuário, que será usado para login.",
        example = "joao.silva@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    @Schema(
        description = "Senha de acesso do usuário (mínimo 6 caracteres).",
        example = "senhaforte123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String senha;

    @NotNull(message = "A role é obrigatória.")
    @Schema(
        description = "Papel do usuário a ser registrado. Opções: CLIENTE, RESTAURANTE, ADMIN.",
        example = "CLIENTE",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UserRole role;
}