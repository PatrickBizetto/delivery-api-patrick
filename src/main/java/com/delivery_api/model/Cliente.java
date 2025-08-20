package com.delivery_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private boolean ativo;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos;

    public Cliente(String nome, String email, String telefone, String endereco, boolean ativo) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.ativo = ativo;
    }

    // Método utilitário para inativação (soft delete)
    public void inativar() {
        this.ativo = false;
    }
}
