package com.delivery_api.controller;

import com.delivery_api.dto.RestauranteDTO;
import com.delivery_api.dto.RestauranteResponseDTO;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.exception.GlobalExceptionHandler;
import com.delivery_api.service.RestauranteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal; // Importe BigDecimal

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para RestauranteController")
public class RestauranteControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RestauranteService restauranteService;

    @InjectMocks
    private RestauranteController restauranteController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restauranteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve retornar status 200 e os dados do restaurante quando o ID existe")
    void deveRetornar200_QuandoBuscarRestauranteExistente() throws Exception {
        long idExistente = 1L;
        RestauranteResponseDTO responseDTO = new RestauranteResponseDTO();
        responseDTO.setId(idExistente);
        responseDTO.setNome("Pizza Place");
        when(restauranteService.buscarRestaurantePorId(idExistente)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/restaurantes/{id}", idExistente))
                .andExpect(status().isOk())
                // CORREÇÃO: Acessar os campos dentro do objeto 'data' (assumindo que você usa um wrapper)
                .andExpect(jsonPath("$.data.id").value(idExistente))
                .andExpect(jsonPath("$.data.nome").value("Pizza Place"));
    }

    @Test
    @DisplayName("Deve retornar status 404 quando buscar um restaurante que não existe")
    void deveRetornar404_QuandoBuscarRestauranteInexistente() throws Exception {
        long idInexistente = 999L;
        when(restauranteService.buscarRestaurantePorId(idInexistente))
                .thenThrow(new EntityNotFoundException("Restaurante", idInexistente));

        mockMvc.perform(get("/api/restaurantes/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Deve retornar status 201 e os dados do restaurante quando criado com sucesso")
    void deveRetornar201_QuandoCriarRestauranteComDadosValidos() throws Exception {
        // CORREÇÃO: Preencher TODOS os campos obrigatórios do DTO para passar na validação
        RestauranteDTO requestDTO = new RestauranteDTO();
        requestDTO.setNome("Novo Restaurante");
        requestDTO.setCategoria("ITALIANA");
        requestDTO.setEndereco("Rua Nova, 123");
        requestDTO.setTelefone("11912345678");
        requestDTO.setTaxaEntrega(BigDecimal.valueOf(5.99));
        requestDTO.setTempoEntrega(40);
        requestDTO.setHorarioFuncionamento("18:00-23:00");

        RestauranteResponseDTO responseDTO = new RestauranteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Novo Restaurante");

        when(restauranteService.cadastrarRestaurante(any(RestauranteDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                // CORREÇÃO: Acessar os campos dentro do objeto 'data'
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.nome").value("Novo Restaurante"));
    }

    @Test
    @DisplayName("Deve retornar status 400 quando tentar criar um restaurante com dados inválidos (nome em branco)")
    void deveRetornar400_QuandoCriarRestauranteComDadosInvalidos() throws Exception {
        RestauranteDTO requestDTO = new RestauranteDTO();
        requestDTO.setNome(""); // Nome inválido
        // Preencher outros campos para isolar o erro apenas no nome
        requestDTO.setCategoria("ITALIANA");
        requestDTO.setEndereco("Rua Nova, 123");
        requestDTO.setTelefone("11912345678");
        requestDTO.setTaxaEntrega(BigDecimal.valueOf(5.99));
        requestDTO.setTempoEntrega(40);
        requestDTO.setHorarioFuncionamento("18:00-23:00");

        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Deve retornar status 204 quando deletar um restaurante existente")
    void deveRetornar204_QuandoDeletarRestauranteExistente() throws Exception {
        long idExistente = 1L;
        doNothing().when(restauranteService).deletarRestaurante(idExistente);

        mockMvc.perform(delete("/api/restaurantes/{id}", idExistente))
                .andExpect(status().isNoContent());
    }
}