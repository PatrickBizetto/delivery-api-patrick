package com.delivery_api.controller;

import com.delivery_api.dto.ClienteDTO;
import com.delivery_api.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser; // 🔹 IMPORT NECESSÁRIO
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();

        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente Teste");
        clienteDTO.setEmail("teste@example.com");
        clienteDTO.setTelefone("11987654321");
        clienteDTO.setEndereco("Rua dos Testes, 100");
    }

    @Test
    @DisplayName("Deve criar um cliente com dados válidos e retornar Status 201 Created")
    @WithMockUser(roles = "ADMIN") // 🔹 SIMULA UM USUÁRIO COM ROLE_ADMIN LOGADO
    void criarCliente_ComDadosValidos_RetornaStatus201() throws Exception {
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                // Verificando o corpo da resposta diretamente
                .andExpect(jsonPath("$.nome", is("Cliente Teste")))
                .andExpect(jsonPath("$.email", is("teste@example.com")));
    }
    
    @Test
    @DisplayName("Deve retornar Status 400 Bad Request ao tentar criar cliente com email inválido")
    @WithMockUser(roles = "ADMIN") // 🔹 SIMULA UM USUÁRIO COM ROLE_ADMIN LOGADO
    void criarCliente_ComEmailInvalido_RetornaStatus400() throws Exception {
        clienteDTO.setEmail(null); 

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isBadRequest());
    }
}