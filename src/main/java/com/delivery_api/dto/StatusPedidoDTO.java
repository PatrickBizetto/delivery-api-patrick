package com.delivery_api.dto;

import com.delivery_api.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public class StatusPedidoDTO {

    @NotNull(message = "O status não pode ser nulo")
    private StatusPedido status;

    // Getters e Setters
    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }
}