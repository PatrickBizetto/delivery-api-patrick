package com.delivery_api.service;

import com.delivery_api.dto.ProdutoDTO;
import com.delivery_api.dto.ProdutoResponseDTO;
import java.util.List;

public interface ProdutoService {

    ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto);

    ProdutoResponseDTO buscarProdutoPorId(Long id);

    ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto);

    void removerProduto(Long id);

    ProdutoResponseDTO alterarDisponibilidade(Long id);

    List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria);

    List<ProdutoResponseDTO> buscarProdutosPorNome(String nome);
    
    List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel);
}