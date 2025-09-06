package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para representar os dados de um produto ao ser retornado pela API.")
public class ProdutoResponseDTO {

    @Schema(description = "ID único do produto.", example = "101")
    private Long id;

    @Schema(description = "Nome do produto.", example = "Pizza Margherita")
    private String nome;

    @Schema(description = "Descrição detalhada do produto.", example = "Molho de tomate fresco, mussarela de búfala e manjericão.")
    private String descricao;

    @Schema(description = "Preço do produto em Reais (R$).", example = "59.90")
    private BigDecimal preco;

    @Schema(description = "Categoria à qual o produto pertence.", example = "Pizzas Tradicionais")
    private String categoria;

    @Schema(description = "Indica se o produto está disponível para venda no momento.", example = "true")
    private boolean disponivel;

    @Schema(description = "ID do restaurante ao qual este produto pertence.", example = "1")
    private Long restauranteId;
}