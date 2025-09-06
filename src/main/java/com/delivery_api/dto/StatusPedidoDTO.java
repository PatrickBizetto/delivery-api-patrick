package com.delivery_api.dto;

import com.delivery_api.enums.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO utilizado para atualizar o status de um pedido.")
public class StatusPedidoDTO {

    @NotNull(message = "O status não pode ser nulo.")
    @Schema(
        description = "O novo status a ser atribuído ao pedido.",
        example = "PREPARANDO",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private StatusPedido status;
}