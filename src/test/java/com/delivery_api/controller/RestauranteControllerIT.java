package com.delivery_api.controller;

import com.delivery_api.dto.RestauranteDTO;
import com.delivery_api.model.Restaurante;
import com.delivery_api.repository.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach; // JUnit 5
import org.junit.jupiter.api.Test;       // JUnit 5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

// Imports estáticos para os métodos do MockMvc
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RestauranteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestauranteRepository restauranteRepository;

    private RestauranteDTO restauranteDTO;
    private Restaurante restauranteSalvo;

    @BeforeEach
    void setUp() {
        restauranteRepository.deleteAll();

        restauranteDTO = new RestauranteDTO();
        restauranteDTO.setNome("Pizza Express");
        restauranteDTO.setCategoria("Italiana");
        restauranteDTO.setEndereco("Rua das Flores, 123");
        restauranteDTO.setTelefone("11999999999");
        restauranteDTO.setTaxaEntrega(new BigDecimal("5.50"));
        restauranteDTO.setTempoEntrega(45);
        restauranteDTO.setHorarioFuncionamento("08:00-22:00");

        // Criar restaurante para testes de busca
        Restaurante restaurante = new Restaurante();
        restaurante.setNome("Burger King");
        restaurante.setCategoria("Americana");
        restaurante.setEndereco("Av. Paulista, 1000");
        restaurante.setTelefone("11888888888");
        restaurante.setTaxaEntrega(new BigDecimal("4.00"));
        restaurante.setTempoEntrega(30);
        restaurante.setHorarioFuncionamento("10:00-23:00");
        restaurante.setAtivo(true);
        restauranteSalvo = restauranteRepository.save(restaurante);
    }

    @Test
    void deveCadastrarRestauranteComSucesso() throws Exception {
        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Pizza Express"))
                .andExpect(jsonPath("$.data.categoria").value("Italiana"))
                .andExpect(jsonPath("$.data.ativo").value(true))
                .andExpect(jsonPath("$.message").value("Restaurante criado com sucesso"));
    }

    @Test
    void deveRejeitarRestauranteComDadosInvalidos() throws Exception {
        restauranteDTO.setNome(""); // Nome inválido
        restauranteDTO.setTelefone("123"); // Telefone inválido

        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isBadRequest());
                // Ajuste: A validação do Spring geralmente não retorna um JSON customizado por padrão,
                // a menos que você configure um ControllerAdvice. Um teste de status 400 é suficiente.
    }

    @Test
    void deveBuscarRestaurantePorId() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}", restauranteSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(restauranteSalvo.getId()))
                .andExpect(jsonPath("$.data.nome").value("Burger King"))
                .andExpect(jsonPath("$.data.categoria").value("Americana"));
    }

    @Test
    void deveRetornar404ParaRestauranteInexistente() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarRestaurantesComPaginacao() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void deveAtualizarRestauranteComSucesso() throws Exception {
        restauranteDTO.setNome("Pizza Express Atualizada");

        mockMvc.perform(put("/api/restaurantes/{id}", restauranteSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Pizza Express Atualizada"))
                .andExpect(jsonPath("$.message").value("Restaurante atualizado com sucesso"));
    }

    @Test
    void deveAlterarStatusRestaurante() throws Exception {
        mockMvc.perform(patch("/api/restaurantes/{id}/status", restauranteSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ativo").value(false))
                .andExpect(jsonPath("$.message").value("Status alterado com sucesso"));
    }

    @Test
    void deveBuscarRestaurantesPorCategoria() throws Exception {
        mockMvc.perform(get("/api/restaurantes/categoria/{categoria}", "Americana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].categoria").value("Americana"));
    }
}