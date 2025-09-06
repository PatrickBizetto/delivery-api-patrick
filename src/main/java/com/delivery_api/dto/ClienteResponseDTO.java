package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO para exibir os dados de um cliente.")
public class ClienteResponseDTO {

    @Schema(description = "ID único do cliente.", example = "1")
    private Long id;

    @Schema(description = "Nome completo do cliente.", example = "Ana Clara Souza")
    private String nome;

    @Schema(description = "E-mail de contato e login do cliente.", example = "ana.souza@example.com")
    private String email;

    @Schema(description = "Número de telefone para contato.", example = "11912345678")
    private String telefone;

    @Schema(description = "Endereço principal de entrega do cliente.", example = "Rua das Palmeiras, 45, Apto 101, Rio de Janeiro, RJ")
    private String endereco;

    @Schema(description = "Indica se o cadastro do cliente está ativo no sistema.", example = "true")
    private boolean ativo;

}