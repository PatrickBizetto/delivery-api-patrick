package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Representa um item individual dentro de um pedido, com seu produto e quantidade.")
public class ItemPedidoDTO {

    @NotNull(message = "O ID do produto é obrigatório.")
    @Positive(message = "O ID do produto deve ser um número positivo.")
    @Schema(description = "ID do produto que está sendo pedido.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long produtoId;

    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1, message = "A quantidade mínima para um item é 1.")
    @Max(value = 50, message = "A quantidade máxima para um item é 50.")
    @Schema(description = "Quantidade de unidades do produto a ser pedido.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantidade;

    @Size(max = 200, message = "As observações não podem exceder 200 caracteres.")
    @Schema(description = "Observações opcionais para o item do pedido (ex: sem cebola, ponto da carne, etc).", example = "Capricha na batata frita!")
    private String observacoes;

}