package com.delivery_api.service;

import com.delivery_api.model.Restaurante;
import java.util.List;
import java.util.Optional;

public interface RestauranteService {

    Restaurante cadastrar(Restaurante restaurante);

    Optional<Restaurante> buscarPorId(Long id);

    List<Restaurante> listarAtivos();

    List<Restaurante> buscarPorCategoria(String categoria);

    Restaurante atualizar(Long id, Restaurante restauranteAtualizado);

    void inativar(Long id);
}