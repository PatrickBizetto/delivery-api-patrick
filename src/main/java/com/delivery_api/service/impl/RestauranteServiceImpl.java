package com.delivery_api.service.impl;

import com.delivery_api.dto.RestauranteDTO;
import com.delivery_api.dto.RestauranteResponseDTO;
import com.delivery_api.exception.ConflictException;
import com.delivery_api.model.Restaurante;
import com.delivery_api.model.Usuario;
import com.delivery_api.repository.RestauranteRepository;
import com.delivery_api.service.RestauranteService;
import com.delivery_api.exception.EntityNotFoundException; 
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 🔹 Imports necessários para o cache
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestauranteServiceImpl implements RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    // 🔹 Invalida o cache de categorias sempre que um novo restaurante é criado.
    @Override
    @CacheEvict(value = "restaurantesPorCategoria", allEntries = true)
    public RestauranteResponseDTO cadastrarRestaurante(RestauranteDTO dto) {
        if (restauranteRepository.existsByTelefone(dto.getTelefone())) {
            throw new ConflictException("Telefone já cadastrado no sistema.", "telefone", dto.getTelefone());
        }
        
        Restaurante restaurante = modelMapper.map(dto, Restaurante.class);
        restaurante.setAtivo(true);
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return modelMapper.map(restauranteSalvo, RestauranteResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> listarRestaurantes(String categoria, Boolean ativo, Pageable pageable) {
        // Caching de resultados paginados é complexo, então optamos por não cachear esta listagem geral.
        Page<Restaurante> restaurantesPage = restauranteRepository.findAll(pageable);
        return restaurantesPage.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }

    // 🔹 Armazena o resultado no cache "restaurantes" usando o ID como chave.
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "restaurantes", key = "#id")
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        System.out.println("### BUSCANDO RESTAURANTE DO BANCO DE DADOS (ID: " + id + ") ###");
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", id)); 
        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    // 🔹 Invalida múltiplos caches: o do restaurante específico pelo ID e o de todas as listas de categorias.
    @Override
    @Caching(evict = {
        @CacheEvict(value = "restaurantes", key = "#id"),
        @CacheEvict(value = "restaurantesPorCategoria", allEntries = true)
    })
    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteDTO dto) {
        System.out.println("### ATUALIZANDO RESTAURANTE E LIMPANDO CACHE (ID: " + id + ") ###");
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        
        modelMapper.map(dto, restaurante);
        
        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);
        return modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
    }

    // 🔹 A alteração de status também invalida os caches.
    @Override
    @Caching(evict = {
        @CacheEvict(value = "restaurantes", key = "#id"),
        @CacheEvict(value = "restaurantesPorCategoria", allEntries = true)
    })
    public RestauranteResponseDTO alterarStatusRestaurante(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        
        restaurante.setAtivo(!restaurante.isAtivo());
        
        restauranteRepository.save(restaurante);
        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    // 🔹 Armazena a lista de restaurantes no cache "restaurantesPorCategoria", usando a categoria como chave.
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "restaurantesPorCategoria", key = "#categoria")
    public List<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria) {
        System.out.println("### BUSCANDO RESTAURANTES POR CATEGORIA DO BANCO (CATEGORIA: " + categoria + ") ###");
        List<Restaurante> restaurantes = restauranteRepository.findByCategoriaAndAtivoTrue(categoria);
        return restaurantes.stream()
                .map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calcularTaxaEntrega(Long id, String cep) {
        restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        // TODO: Implementar lógica de cálculo de frete
        return new BigDecimal("10.00");
    }
    
    @Override
    public List<RestauranteResponseDTO> buscarRestaurantesProximos(String cep, Integer raio) {
        // TODO: Implementar lógica de busca por proximidade
        return List.of();
    }
    
    // 🔹 Deletar um restaurante também invalida os caches.
    @Override
    @Caching(evict = {
        @CacheEvict(value = "restaurantes", key = "#id"),
        @CacheEvict(value = "restaurantesPorCategoria", allEntries = true)
    })
    public void deletarRestaurante(Long id) {
        if (!restauranteRepository.existsById(id)) {
            throw new EntityNotFoundException("Restaurante", id);
        }
        restauranteRepository.deleteById(id);
    }
    
    // MÉTODO DE VERIFICAÇÃO DE SEGURANÇA - Não precisa de cache
    public boolean isOwner(Long restauranteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) principal;
            return usuarioLogado.getRestauranteId() != null && usuarioLogado.getRestauranteId().equals(restauranteId);
        }

        return false;
    }
}