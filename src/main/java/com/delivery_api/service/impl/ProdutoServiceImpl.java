package com.delivery_api.service.impl;

import com.delivery_api.dto.ProdutoDTO;
import com.delivery_api.dto.ProdutoResponseDTO;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.model.Produto;
import com.delivery_api.model.Restaurante;
import com.delivery_api.repository.ProdutoRepository;
import com.delivery_api.repository.RestauranteRepository;
import com.delivery_api.service.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProdutoServiceImpl implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + dto.getRestauranteId()));
        
        Produto produto = modelMapper.map(dto, Produto.class);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        
        Produto produtoSalvo = produtoRepository.save(produto);
        return modelMapper.map(produtoSalvo, ProdutoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    @Override
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
        
        // Garante que o restaurante não seja alterado na atualização do produto
        dto.setRestauranteId(null); 
        modelMapper.map(dto, produto);
        
        Produto produtoAtualizado = produtoRepository.save(produto);
        return modelMapper.map(produtoAtualizado, ProdutoResponseDTO.class);
    }

    @Override
    public void removerProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado: " + id);
        }
        // Adicionar verificação se o produto está em algum pedido antes de deletar
        produtoRepository.deleteById(id);
    }

    @Override
    public ProdutoResponseDTO alterarDisponibilidade(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
        produto.setDisponivel(!produto.isDisponivel()); // Inverte o status
        produtoRepository.save(produto);
        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        List<Produto> produtos = produtoRepository.findByCategoriaAndDisponivelTrue(categoria);
        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorNome(String nome) {
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue(nome);
        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel) {
        List<Produto> produtos;
        if (disponivel != null && disponivel) {
            produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
        } else {
            produtos = produtoRepository.findByRestauranteId(restauranteId);
        }
        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }
}