package com.delivery_api.dto;

import com.delivery_api.validation.ValidCEP;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO contendo todos os dados necessários para a criação de um novo pedido.")
public class PedidoDTO {

    @Schema(description = "ID do cliente que está realizando o pedido.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O ID do cliente é obrigatório.")
    @Positive(message = "O ID do cliente deve ser um número positivo.")
    private Long clienteId;

    @Schema(description = "ID do restaurante de onde o pedido se origina.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O ID do restaurante é obrigatório.")
    @Positive(message = "O ID do restaurante deve ser um número positivo.")
    private Long restauranteId;

    @Schema(description = "Endereço completo para a entrega do pedido.", example = "Rua das Flores, 123, Apto 4B, Centro", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O endereço de entrega é obrigatório.")
    @Size(min = 10, max = 200, message = "O endereço deve ter entre 10 e 200 caracteres.")
    private String enderecoEntrega;

    @Schema(description = "CEP do endereço de entrega. Formato: XXXXX-XXX", example = "16015-240", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O CEP é obrigatório.")
    @ValidCEP // Validação customizada
    private String cep;

    @Schema(description = "Forma de pagamento escolhida para o pedido.", example = "CARTAO_CREDITO", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO", "PIX"})
    @NotBlank(message = "A forma de pagamento é obrigatória.")
    @Pattern(regexp = "^(DINHEIRO|CARTAO_CREDITO|CARTAO_DEBITO|PIX)$",
             message = "Forma de pagamento inválida. Valores aceitos: DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX")
    private String formaPagamento;

    @Schema(description = "Observações adicionais para o preparo ou entrega do pedido (opcional).", example = "Sem cebola, por favor.")
    @Size(max = 500, message = "As observações não podem exceder 500 caracteres.")
    private String observacoes;

    @Schema(description = "Lista dos itens que compõem o pedido. A lista não pode estar vazia.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "A lista de itens não pode estar vazia.")
    @Valid // Assegura que cada ItemPedidoDTO dentro da lista seja validado individualmente.
    private List<ItemPedidoDTO> itens;
}