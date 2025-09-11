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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    /**
     * Limpa todos os caches de "produtos" sempre que um novo produto é cadastrado.
     * Isso garante que qualquer lista de produtos em cache seja atualizada na próxima requisição.
     */
    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    public ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + dto.getRestauranteId()));
        
        Produto produto = modelMapper.map(dto, Produto.class);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        
        Produto produtoSalvo = produtoRepository.save(produto);
        System.out.println("### CADASTRANDO PRODUTO E LIMPANDO CACHE DE LISTAS ###");
        return modelMapper.map(produtoSalvo, ProdutoResponseDTO.class);
    }

    /**
     * Armazena o resultado da busca por ID no cache "produtos".
     * A chave será o ID do produto. Evita buscas repetidas no banco para o mesmo produto.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "#id")
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        System.out.println("### BUSCANDO PRODUTO DO BANCO DE DADOS (ID: " + id + ") ###");
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    /**
     * Invalida o cache para um produto específico (pelo ID) e também limpa
     * todos os caches de listas de produtos para refletir a alteração.
     */
    @Override
    @Caching(evict = {
        @CacheEvict(value = "produtos", key = "#id"),
        @CacheEvict(value = "produtos", allEntries = true)
    })
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());

        Produto produtoAtualizado = produtoRepository.save(produto);
        System.out.println("### ATUALIZANDO PRODUTO E LIMPANDO SEU CACHE (ID: " + id + ") ###");
        return modelMapper.map(produtoAtualizado, ProdutoResponseDTO.class);
    }

    /**
     * Invalida o cache de um produto ao removê-lo.
     */
    @Override
    @Caching(evict = {
        @CacheEvict(value = "produtos", key = "#id"),
        @CacheEvict(value = "produtos", allEntries = true)
    })
    public void removerProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado: " + id);
        }
        produtoRepository.deleteById(id);
        System.out.println("### REMOVENDO PRODUTO E LIMPANDO SEU CACHE (ID: " + id + ") ###");
    }

    /**
     * Invalida o cache do produto cuja disponibilidade foi alterada.
     */
    @Override
    @Caching(evict = {
        @CacheEvict(value = "produtos", key = "#id"),
        @CacheEvict(value = "produtos", allEntries = true)
    })
    public ProdutoResponseDTO alterarDisponibilidade(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
        produto.setDisponivel(!produto.isDisponivel());
        produtoRepository.save(produto);
        System.out.println("### ALTERANDO DISPONIBILIDADE E LIMPANDO CACHE (ID: " + id + ") ###");
        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    /**
     * Cacheia a lista completa de todos os produtos.
     * A chave 'todos' é estática para identificar essa busca específica.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "'todos'")
    public List<ProdutoResponseDTO> listarTodosProdutos() {
        System.out.println("### BUSCANDO TODOS OS PRODUTOS DO BANCO DE DADOS ###");
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Cacheia a busca de produtos por categoria.
     * A chave é composta para ser única para cada categoria.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "'categoria::' + #categoria")
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        System.out.println("### BUSCANDO PRODUTOS POR CATEGORIA DO BANCO: " + categoria + " ###");
        List<Produto> produtos = produtoRepository.findByCategoriaAndDisponivelTrue(categoria);
        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "'nome::' + #nome")
    public List<ProdutoResponseDTO> buscarProdutosPorNome(String nome) {
        System.out.println("### BUSCANDO PRODUTOS POR NOME DO BANCO: " + nome + " ###");
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue(nome);
        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "'restaurante::' + #restauranteId + '::disponivel::' + #disponivel")
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel) {
        System.out.println("### BUSCANDO PRODUTOS POR RESTAURANTE DO BANCO: " + restauranteId + " ###");
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

    // Este método é de lógica de negócio/segurança, não de recuperação de dados.
    // Geralmente não é um bom candidato para cache, pois precisa ser verificado em tempo real.
    public boolean isOwner(Long produtoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            return false;
        }
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        if (usuarioLogado.getRestauranteId() == null) {
            return false;
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + produtoId + " não encontrado."));

        Long restauranteIdDoUsuario = usuarioLogado.getRestauranteId();
        Long restauranteIdDoProduto = produto.getRestaurante().getId();

        return restauranteIdDoUsuario.equals(restauranteIdDoProduto);
    }
}