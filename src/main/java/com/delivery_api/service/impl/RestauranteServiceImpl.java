package com.delivery_api.service.impl;

import com.delivery_api.dto.RestauranteDTO;
import com.delivery_api.dto.RestauranteResponseDTO;
import com.delivery_api.model.Restaurante; // Assumindo que sua entidade está em com.delivery_api.model
import com.delivery_api.repository.RestauranteRepository;
import com.delivery_api.service.RestauranteService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestauranteServiceImpl implements RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ModelMapper modelMapper; // Injeta o ModelMapper

    @Override
    public RestauranteResponseDTO cadastrarRestaurante(RestauranteDTO dto) {
        // Converte o DTO para a Entidade
        Restaurante restaurante = modelMapper.map(dto, Restaurante.class);
        restaurante.setAtivo(true); // Regra de negócio: sempre começa como ativo
        
        // Salva a entidade no banco
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        
        // Converte a Entidade salva de volta para o DTO de resposta
        return modelMapper.map(restauranteSalvo, RestauranteResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> listarRestaurantes(String categoria, Boolean ativo, Pageable pageable) {
        // Lógica de busca com filtros será necessária no repositório
        // Por enquanto, vamos usar o findAll como exemplo
        Page<Restaurante> restaurantesPage = restauranteRepository.findAll(pageable);
        
        // Mapeia a página de Entidades para uma página de DTOs
        return restaurantesPage.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));
        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    @Override
    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));
        
        // Usa o modelMapper para atualizar os campos do objeto existente
        modelMapper.map(dto, restaurante);
        
        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);
        return modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
    }

    @Override
    public RestauranteResponseDTO alterarStatusRestaurante(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));
        
        restaurante.setAtivo(!restaurante.isAtivo()); // Inverte o status atual
        
        restauranteRepository.save(restaurante);
        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria) {
        List<Restaurante> restaurantes = restauranteRepository.findByCategoriaAndAtivoTrue(categoria);
        return restaurantes.stream()
                .map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Métodos abaixo são exemplos e precisam de lógica real
    @Override
    public BigDecimal calcularTaxaEntrega(Long id, String cep) {
        restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));
        // TODO: Implementar lógica de cálculo de frete (ex: consultar API externa)
        return new BigDecimal("10.00"); // Valor de exemplo
    }
    
    @Override
    public List<RestauranteResponseDTO> buscarRestaurantesProximos(String cep, Integer raio) {
        // TODO: Implementar lógica de busca por proximidade (geolocalização)
        return List.of(); // Retorna lista vazia como exemplo
    }
}