package com.delivery_api.dto;

import com.delivery_api.enums.UserRole;
import com.delivery_api.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para exibir os dados de um usuário após login ou consulta.")
public class UserResponse {

    @Schema(description = "ID único do usuário.", example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário.", example = "Maria Oliveira")
    private String nome;

    @Schema(description = "Endereço de e-mail do usuário (usado para login).", example = "maria.oliveira@example.com")
    private String email;

    @Schema(description = "Papel (permissão) do usuário no sistema.", example = "CLIENTE")
    private UserRole role; 

    @Schema(description = "ID do restaurante associado ao usuário, se aplicável (para usuários com role RESTAURANTE).", example = "25", nullable = true)
    private Long restauranteId;

    public UserResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
        this.restauranteId = usuario.getRestauranteId();
    }
}