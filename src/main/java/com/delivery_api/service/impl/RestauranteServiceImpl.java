package com.delivery_api.service.impl;

import com.delivery_api.dto.RestauranteDTO;
import com.delivery_api.dto.RestauranteResponseDTO;
import com.delivery_api.exception.ConflictException;
import com.delivery_api.model.Restaurante;
import com.delivery_api.repository.RestauranteRepository;
import com.delivery_api.service.RestauranteService;
import com.delivery_api.exception.EntityNotFoundException; 
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
    private ModelMapper modelMapper;

    @Override
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
        Page<Restaurante> restaurantesPage = restauranteRepository.findAll(pageable);
        return restaurantesPage.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                // <<-- CORREÇÃO 2: AGORA ESTA LINHA USA A EXCEÇÃO CORRETA
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", id)); 
        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    @Override
    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        
        modelMapper.map(dto, restaurante);
        
        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);
        return modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
    }

    @Override
    public RestauranteResponseDTO alterarStatusRestaurante(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        
        restaurante.setAtivo(!restaurante.isAtivo());
        
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

    @Override
    public void deletarRestaurante(Long id) {
        if (!restauranteRepository.existsById(id)) {
            throw new EntityNotFoundException("Restaurante", id);
        }
        restauranteRepository.deleteById(id);
    }
}