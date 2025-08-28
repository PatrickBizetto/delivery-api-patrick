package com.delivery_api.dto;

import lombok.Data;
import java.math.BigDecimal;

// Usando @Data do Lombok para gerar getters, setters, etc.
@Data
public class RestauranteResponseDTO {
    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private String telefone;
    private BigDecimal taxaEntrega;
    private Integer tempoEntrega;
    private String horarioFuncionamento;
    private boolean ativo;
}