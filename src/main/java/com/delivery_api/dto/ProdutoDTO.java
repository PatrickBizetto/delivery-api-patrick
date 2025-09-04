package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Dados para cadastro ou atualização de um produto")
public class ProdutoDTO {

    @Schema(description = "Nome do produto", example = "Pizza Margherita", required = true)
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Schema(description = "Descrição detalhada do produto", example = "Molho de tomate fresco, mussarela de búfala e manjericão")
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 255, message = "Descrição deve ter entre 10 e 255 caracteres")
    private String descricao;

    @Schema(description = "Preço do produto", example = "45.50", required = true)
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @DecimalMax(value = "500.00", message = "Preço não pode exceder R$ 500,00")
    private BigDecimal preco;

    @Schema(description = "Categoria do produto", example = "Pizza Salgada", required = true)
    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1", required = true)
    @NotNull(message = "ID do restaurante é obrigatório")
    @Positive(message = "ID do restaurante deve ser um número positivo")
    private Long restauranteId;

    @Schema(description = "URL de uma imagem do produto (opcional)", example = "https://example.com/images/pizza.jpg")
    @Pattern(regexp = "^(https?://).+\\.(jpg|jpeg|png|gif)$",
             message = "URL da imagem deve ser válida e ter formato JPG, JPEG, PNG ou GIF")
    private String imagemUrl;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
}