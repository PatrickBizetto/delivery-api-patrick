package com.delivery_api.dto;

import com.delivery_api.model.Usuario;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private com.delivery_api.enums.Role role;
    private Long restauranteId;

    public UserResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
        this.restauranteId = usuario.getRestauranteId();
    }
}