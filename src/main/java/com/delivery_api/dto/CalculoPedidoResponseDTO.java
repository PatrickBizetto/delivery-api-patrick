package com.delivery_api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CalculoPedidoResponseDTO {
    
    private BigDecimal subtotalItens;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;
}