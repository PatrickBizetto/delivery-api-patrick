package com.delivery_api.model;

import com.delivery_api.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroPedido;
    private LocalDateTime dataPedido;
    private String enderecoEntrega;
    private BigDecimal subtotal;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;
    private String observacoes;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
        item.setPedido(this);
        recalcularTotais();
    }

    public void recalcularTotais() {
        this.subtotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.taxaEntrega = this.restaurante.getTaxaEntrega();
        this.valorTotal = this.subtotal.add(this.taxaEntrega);
    }

    public void confirmar() {
        this.setStatus(StatusPedido.CONFIRMADO);
        this.setDataPedido(LocalDateTime.now());
        this.setNumeroPedido("PED-" + UUID.randomUUID().toString().toUpperCase());
        this.setEnderecoEntrega(this.cliente.getEndereco());
        recalcularTotais();
    }
}