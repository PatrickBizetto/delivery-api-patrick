package com.delivery_api.dto;

import com.delivery_api.enums.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "DTO para exibir os dados detalhados de um pedido.")
public class PedidoResponseDTO {

    @Schema(description = "ID único do pedido.", example = "101")
    private Long id;

    @Schema(description = "Data e hora em que o pedido foi realizado.")
    private LocalDateTime dataPedido;

    @Schema(description = "Status atual do pedido.", example = "CONFIRMADO")
    private StatusPedido status;

    @Schema(description = "Endereço completo para a entrega do pedido.", example = "Rua das Flores, 123, Apto 45, Bairro, Cidade - UF")
    private String enderecoEntrega;

    @Schema(description = "Valor total dos itens do pedido, sem a taxa de entrega.", example = "89.90")
    private BigDecimal subtotal;

    @Schema(description = "Custo da entrega.", example = "7.50")
    private BigDecimal taxaEntrega;

    @Schema(description = "Valor final do pedido (subtotal + taxa de entrega).", example = "97.40")
    private BigDecimal valorTotal;

    @Schema(description = "Informações resumidas do cliente que fez o pedido.")
    private ClienteResumidoDTO cliente;

    @Schema(description = "Informações resumidas do restaurante responsável pelo pedido.")
    private RestauranteResumidoDTO restaurante;

    @Schema(description = "Lista de itens que compõem o pedido.")
    private List<ItemPedidoDTO> itens;


    // --- Sub-classes para respostas aninhadas ---

    @Getter
    @Setter
    @Schema(description = "DTO com informações resumidas de um Cliente.")
    public static class ClienteResumidoDTO {

        @Schema(description = "ID do cliente.", example = "42")
        private Long id;

        @Schema(description = "Nome do cliente.", example = "Ana Carolina")
        private String nome;
    }

    @Getter
    @Setter
    @Schema(description = "DTO com informações resumidas de um Restaurante.")
    public static class RestauranteResumidoDTO {

        @Schema(description = "ID do restaurante.", example = "15")
        private Long id;

        @Schema(description = "Nome do restaurante.", example = "Sabor Oriental")
        private String nome;
    }
}