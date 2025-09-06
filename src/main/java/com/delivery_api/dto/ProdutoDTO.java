package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "DTO com os dados necessários para cadastrar ou atualizar um produto.")
public class ProdutoDTO {

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @Schema(description = "Nome do produto.", example = "Pizza Margherita", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotBlank(message = "A descrição do produto é obrigatória.")
    @Size(min = 10, max = 255, message = "A descrição deve ter entre 10 e 255 caracteres.")
    @Schema(description = "Descrição detalhada do produto, incluindo ingredientes.", example = "Molho de tomate fresco, mussarela de búfala e manjericão.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String descricao;

    @NotNull(message = "O preço do produto é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    @DecimalMax(value = "9999.99", message = "O preço máximo permitido é R$ 9999.99.")
    @Schema(description = "Preço de venda do produto.", example = "45.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal preco;

    @NotBlank(message = "A categoria do produto é obrigatória.")
    @Schema(description = "Categoria para agrupar o produto (ex: PIZZA, BEBIDA, SOBREMESA).", example = "PIZZA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;

    @NotNull(message = "O ID do restaurante é obrigatório.")
    @Positive(message = "O ID do restaurante deve ser um número positivo.")
    @Schema(description = "ID do restaurante ao qual o produto pertence.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long restauranteId;

    @Pattern(regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif)$", message = "A URL da imagem deve ser válida e terminar com .jpg, .jpeg, .png ou .gif.")
    @Schema(description = "URL de uma imagem do produto (opcional).", example = "https://i.imgur.com/3g4z6gN.jpg")
    private String imagemUrl;
}