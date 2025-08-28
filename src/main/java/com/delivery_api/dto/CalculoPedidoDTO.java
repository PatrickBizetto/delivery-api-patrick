package com.delivery_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CalculoPedidoDTO {

    @NotNull(message = "ID do restaurante é obrigatório")
    private Long restauranteId;

    @NotEmpty(message = "O pedido deve ter pelo menos um item")
    @Valid // Valida os itens dentro da lista
    private List<ItemPedidoDTO> itens;

    // Getters e Setters
    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }
}