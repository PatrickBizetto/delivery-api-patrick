package com.delivery_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String categoria;
    private String endereco;
    
    @Column(unique = true)
    private String telefone;

    private BigDecimal taxaEntrega;
    private boolean ativo;

    private Integer tempoEntrega;
    private String horarioFuncionamento;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurante")
    private List<Produto> produtos;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurante")
    private List<Pedido> pedidos;

    public Restaurante(String nome, String categoria, String endereco, String telefone, BigDecimal taxaEntrega, boolean ativo, Integer tempoEntrega, String horarioFuncionamento) {
        this.nome = nome;
        this.categoria = categoria;
        this.endereco = endereco;
        this.telefone = telefone;
        this.taxaEntrega = taxaEntrega;
        this.tempoEntrega = tempoEntrega;
        this.horarioFuncionamento = horarioFuncionamento;
        this.ativo = ativo;
    }
}