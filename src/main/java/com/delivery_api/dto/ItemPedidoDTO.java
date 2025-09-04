package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Dados de um item específico dentro de um pedido")
public class ItemPedidoDTO {

    @Schema(description = "ID do produto a ser adicionado", example = "1", required = true)
    @NotNull(message = "Produto ID é obrigatório")
    @Positive(message = "Produto ID deve ser positivo")
    private Long produtoId;

    @Schema(description = "Quantidade do produto", example = "2", required = true)
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    @Max(value = 50, message = "Quantidade não pode exceder 50 unidades")
    private Integer quantidade;

    @Schema(description = "Observações específicas para este item (opcional)", example = "Sem picles")
    @Size(max = 200, message = "Observações não podem exceder 200 caracteres")
    private String observacoes;

    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}