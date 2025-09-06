package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO para transferir dados de criação ou atualização de um Cliente.")
public class ClienteDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Schema(description = "Nome completo do cliente.",
            example = "Carlos Alberto de Nóbrega",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Schema(description = "Endereço de e-mail do cliente. Deve ser único.",
            example = "carlos.nobrega@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos, sem formatação.")
    @Schema(description = "Número de telefone do cliente, apenas dígitos.",
            example = "11912345678",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    @Schema(description = "Endereço completo para entrega.",
            example = "Rua das Palmeiras, 45, Apto 101, Bairro Feliz, São Paulo - SP",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String endereco;

}