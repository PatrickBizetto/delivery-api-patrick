package com.delivery_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    // Método de negócio para calcular o subtotal do item
    public void calcularSubtotal() {
        if (precoUnitario != null && quantidade > 0) {
            this.subtotal = precoUnitario.multiply(new BigDecimal(quantidade));
        }
    }
}
