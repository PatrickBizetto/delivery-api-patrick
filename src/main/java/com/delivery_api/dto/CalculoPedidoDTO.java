package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Schema(description = "DTO utilizado para enviar os dados necessários para o cálculo do valor total de um pedido, sem a necessidade de criá-lo.")
public class CalculoPedidoDTO {

    @NotNull(message = "ID do restaurante é obrigatório")
    @Schema(description = "ID do restaurante de onde os produtos são.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long restauranteId;

    @NotEmpty(message = "O pedido deve ter pelo menos um item")
    @Valid // Assegura que os objetos ItemPedidoDTO dentro da lista sejam validados
    @Schema(description = "Lista dos itens que compõem o pedido para o cálculo.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ItemPedidoDTO> itens;
}