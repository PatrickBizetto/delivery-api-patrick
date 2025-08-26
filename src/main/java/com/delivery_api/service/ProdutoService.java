package com.delivery_api.service;

import com.delivery_api.model.Produto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProdutoService {

    Produto cadastrar(Produto produto, Long restauranteId);

    Optional<Produto> buscarPorId(Long id);

    List<Produto> listarPorRestaurante(Long restauranteId);

    List<Produto> buscarPorCategoria(String categoria);

    Produto atualizar(Long id, Produto produtoAtualizado);

    void alterarDisponibilidade(Long id, boolean disponivel);

    List<Produto> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax);
}