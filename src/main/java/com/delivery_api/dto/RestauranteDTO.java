package com.delivery_api.dto;

import com.delivery_api.validation.ValidCategoria;
import com.delivery_api.validation.ValidHorarioFuncionamento;
import com.delivery_api.validation.ValidTelefone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "DTO com os dados necessários para cadastrar ou atualizar um restaurante.")
public class RestauranteDTO {

    @Schema(description = "Nome do restaurante. Deve ser único.", example = "Pizza Express", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Schema(description = "Categoria do restaurante (ex: ITALIANA, BRASILEIRA, JAPONESA).", example = "ITALIANA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Categoria é obrigatória") // Usei @NotBlank pois é uma String
    @ValidCategoria
    private String categoria;

    @Schema(description = "Endereço completo do restaurante.", example = "Rua das Flores, 123 - Centro", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @Schema(description = "Telefone para contato, apenas números com DDD.", example = "11987654321", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Telefone é obrigatório")
    @ValidTelefone
    private String telefone;

    @Schema(description = "E-mail de contato do restaurante (opcional).", example = "contato@pizzaexpress.com")
    @Email(message = "Email deve ter formato válido")
    private String email;

    @Schema(description = "Taxa de entrega em Reais (R$).", example = "5.50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Taxa de entrega não pode ser negativa") // Permite taxa 0.0 (grátis)
    @DecimalMax(value = "50.0", message = "Taxa de entrega não pode exceder R$ 50,00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Tempo estimado de entrega em minutos.", example = "45", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Tempo de entrega é obrigatório")
    @Min(value = 10, message = "Tempo mínimo é 10 minutos")
    @Max(value = 120, message = "Tempo máximo é 120 minutos")
    private Integer tempoEntrega;

    @Schema(description = "Horário de funcionamento no formato HH:mm-HH:mm.", example = "18:00-23:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Horário de funcionamento é obrigatório")
    @ValidHorarioFuncionamento
    private String horarioFuncionamento;
}