package com.delivery_api.service.impl;

import com.delivery_api.dto.ProdutoDTO;
import com.delivery_api.dto.ProdutoResponseDTO;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.model.Produto;
import com.delivery_api.model.Restaurante;
import com.delivery_api.model.Usuario;
import com.delivery_api.repository.ProdutoRepository;
import com.delivery_api.repository.RestauranteRepository;
import com.delivery_api.service.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // Atualiza apenas os campos permitidos, manualmente.
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
        
        // O campo 'disponivel' é atualizado em outro método, e o 'restaurante' nunca deve mudar.

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
    public List<ProdutoResponseDTO> listarTodosProdutos() {
        // This should return all products from all restaurants
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
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

    public boolean isOwner(Long produtoId) {
        // 1. Obtém o usuário autenticado da sessão.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            return false;
        }
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        // 2. Se o usuário não for de um restaurante, ele não pode ser dono de um produto.
        if (usuarioLogado.getRestauranteId() == null) {
            return false;
        }

        // 3. Busca o produto no banco de dados para encontrar a qual restaurante ele pertence.
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + produtoId + " não encontrado."));

        // 4. Compara o ID do restaurante do usuário logado com o ID do restaurante do produto.
        Long restauranteIdDoUsuario = usuarioLogado.getRestauranteId();
        Long restauranteIdDoProduto = produto.getRestaurante().getId();

        return restauranteIdDoUsuario.equals(restauranteIdDoProduto);
    }
}