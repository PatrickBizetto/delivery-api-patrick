package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO que representa o resultado do cálculo do valor de um pedido, incluindo subtotal, taxa de entrega e valor final.")
public class CalculoPedidoResponseDTO implements Serializable{
    
    @Schema(description = "Soma do valor de todos os itens do pedido, sem a taxa de entrega.", example = "75.50")
    private BigDecimal subtotalItens;

    @Schema(description = "Valor da taxa de entrega aplicada ao pedido.", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Valor final do pedido, correspondente à soma do subtotal dos itens e da taxa de entrega.", example = "80.50")
    private BigDecimal valorTotal;
}