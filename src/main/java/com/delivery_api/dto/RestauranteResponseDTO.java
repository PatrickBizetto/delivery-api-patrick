package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para exibir os dados de um restaurante.")
public class RestauranteResponseDTO {

    @Schema(description = "ID único do restaurante.", example = "1")
    private Long id;

    @Schema(description = "Nome do restaurante.", example = "Cantina da Mama")
    private String nome;

    @Schema(description = "Categoria culinária do restaurante.", example = "Italiana")
    private String categoria;

    @Schema(description = "Endereço completo do restaurante.", example = "Rua das Pizzas, 123, São Paulo - SP")
    private String endereco;

    @Schema(description = "Número de telefone para contato.", example = "11987654321")
    private String telefone;

    @Schema(description = "Valor da taxa de entrega.", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Tempo médio de entrega em minutos.", example = "45")
    private Integer tempoEntrega;

    @Schema(description = "Horário de funcionamento do restaurante.", example = "18:00 - 23:00")
    private String horarioFuncionamento;

    @Schema(description = "Indica se o restaurante está aberto para receber pedidos.", example = "true")
    private boolean ativo;
}