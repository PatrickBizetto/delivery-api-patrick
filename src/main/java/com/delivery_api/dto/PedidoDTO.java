package com.delivery_api.dto;

import com.delivery_api.validation.ValidCEP;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "Dados para criação de um novo pedido")
public class PedidoDTO {

    @Schema(description = "ID do cliente que está fazendo o pedido", example = "1", required = true)
    @NotNull(message = "Cliente ID é obrigatório")
    @Positive(message = "Cliente ID deve ser positivo")
    private Long clienteId;

    @Schema(description = "ID do restaurante de onde o pedido está sendo feito", example = "1", required = true)
    @NotNull(message = "Restaurante ID é obrigatório")
    @Positive(message = "Restaurante ID deve ser positivo")
    private Long restauranteId;

    @Schema(description = "Endereço completo para a entrega", example = "Rua Nova, 987, Bairro Feliz", required = true)
    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Size(max = 200, message = "Endereço não pode exceder 200 caracteres")
    private String enderecoEntrega;

    @Schema(description = "CEP do endereço de entrega", example = "16000-000", required = true)
    @NotBlank(message = "CEP é obrigatório")
    @ValidCEP // Validação customizada para o formato do CEP
    private String cep;

    @Schema(description = "Forma de pagamento escolhida", example = "PIX", required = true,
            allowableValues = {"DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO", "PIX"})
    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Pattern(regexp = "^(DINHEIRO|CARTAO_CREDITO|CARTAO_DEBITO|PIX)$",
             message = "Forma de pagamento deve ser: DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO ou PIX")
    private String formaPagamento;

    @Schema(description = "Observações adicionais para o pedido (opcional)", example = "Tirar a cebola, por favor.")
    @Size(max = 500, message = "Observações não podem exceder 500 caracteres")
    private String observacoes;

    @Schema(description = "Lista de itens do pedido. Não pode estar vazia.", required = true)
    @NotEmpty(message = "Lista de itens não pode estar vazia")
    @Valid // Garante que cada ItemPedidoDTO na lista também será validado
    private List<ItemPedidoDTO> itens;


    // Getters e Setters
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }
}