package com.delivery_api.dto;

import com.delivery_api.enums.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponseDTO {

    private Long id;
    private ClienteResumidoDTO cliente;
    private RestauranteResumidoDTO restaurante;
    private LocalDateTime dataPedido;
    private StatusPedido status;
    private String enderecoEntrega;
    private BigDecimal subtotal;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;
    private List<ItemPedidoDTO> itens;

    // Sub-classes para evitar expor a entidade completa
    public static class ClienteResumidoDTO {
        private Long id;
        private String nome;
        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }

    public static class RestauranteResumidoDTO {
        private Long id;
        private String nome;
        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }


    // Getters e Setters para a classe principal
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ClienteResumidoDTO getCliente() { return cliente; }
    public void setCliente(ClienteResumidoDTO cliente) { this.cliente = cliente; }
    public RestauranteResumidoDTO getRestaurante() { return restaurante; }
    public void setRestaurante(RestauranteResumidoDTO restaurante) { this.restaurante = restaurante; }
    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTaxaEntrega() { return taxaEntrega; }
    public void setTaxaEntrega(BigDecimal taxaEntrega) { this.taxaEntrega = taxaEntrega; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }
}