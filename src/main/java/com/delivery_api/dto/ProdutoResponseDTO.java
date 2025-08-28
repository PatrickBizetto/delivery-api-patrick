package com.delivery_api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data // Anotação do Lombok para gerar Getters, Setters, etc.
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private boolean disponivel;
    private Long restauranteId; // Para saber a qual restaurante pertence
}