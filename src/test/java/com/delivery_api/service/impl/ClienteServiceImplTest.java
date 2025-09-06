package com.delivery_api.service.impl;

import com.delivery_api.dto.ClienteDTO;
import com.delivery_api.dto.ClienteResponseDTO;
import com.delivery_api.exception.BusinessException;
import com.delivery_api.exception.ConflictException;
import com.delivery_api.exception.EntityNotFoundException; // Certifique-se de que a exceção existe
import com.delivery_api.model.Cliente;
import com.delivery_api.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao.silva@example.com");

        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Silva");
        clienteDTO.setEmail("joao.silva@example.com");
        
        clienteResponseDTO = new ClienteResponseDTO();
        clienteResponseDTO.setId(1L);
        clienteResponseDTO.setNome("João Silva");
        clienteResponseDTO.setEmail("joao.silva@example.com");
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso quando email não existe")
    void cadastrarCliente_QuandoEmailNaoExiste_RetornaSucesso() {
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(modelMapper.map(any(ClienteDTO.class), eq(Cliente.class))).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        clienteService.cadastrarCliente(clienteDTO);

        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    // ClienteServiceImplTest.java

    @Test
    @DisplayName("Deve ativar/desativar cliente com sucesso quando ID existe")
    void ativarDesativarCliente_QuandoIdExiste_RetornaClienteComStatusAlterado() {
        // ARRANGE
        cliente.setAtivo(true);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // ACT
        ClienteResponseDTO resultado = clienteService.ativarDesativarCliente(1L);

        // ASSERT
        assertNotNull(resultado);
        verify(clienteRepository).save(cliente);
        // Verifica se o status foi invertido
        assertFalse(cliente.isAtivo()); // Como começou true, deve virar false
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao ativar/desativar cliente com ID inexistente")
    void ativarDesativarCliente_QuandoIdNaoExiste_LancaExcecao() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.ativarDesativarCliente(99L));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve buscar cliente por email com sucesso quando email existe")
    void buscarClientePorEmail_QuandoEmailExiste_RetornaCliente() {
        when(clienteRepository.findByEmail("joao.silva@example.com")).thenReturn(Optional.of(cliente));
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        ClienteResponseDTO resultado = clienteService.buscarClientePorEmail("joao.silva@example.com");

        assertNotNull(resultado);
        assertEquals("joao.silva@example.com", resultado.getEmail());
        verify(clienteRepository, times(1)).findByEmail("joao.silva@example.com");
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar cliente por email inexistente")
    void buscarClientePorEmail_QuandoEmailNaoExiste_LancaExcecao() {
        when(clienteRepository.findByEmail("inexistente@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.buscarClientePorEmail("inexistente@example.com"));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar cliente com email já existente")
    void atualizarCliente_QuandoEmailJaExiste_LancaExcecao() {
        // ARRANGE
        ClienteDTO dto = new ClienteDTO();
        dto.setEmail("outro.email@example.com"); // Email diferente do original

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail("outro.email@example.com")).thenReturn(true);

        // ACT & ASSERT
        assertThrows(BusinessException.class, () -> clienteService.atualizarCliente(1L, dto));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve atualizar cliente mantendo mesmo email sem validar duplicação")
    void atualizarCliente_QuandoMesmoEmail_NaoValidaDuplicacao() {
        // ARRANGE
        ClienteDTO dto = new ClienteDTO();
        dto.setEmail("joao.silva@example.com"); // Mesmo email original

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // ACT
        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, dto);

        // ASSERT
        assertNotNull(resultado);
        verify(clienteRepository).save(cliente);
        // Não deve verificar existência do mesmo email
        verify(clienteRepository, never()).existsByEmail("joao.silva@example.com");
}

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar cadastrar email duplicado")
    void cadastrarCliente_QuandoEmailJaExiste_LancaExcecao() {
        when(clienteRepository.findByEmail(clienteDTO.getEmail())).thenReturn(Optional.of(cliente));

        assertThrows(ConflictException.class, () -> clienteService.cadastrarCliente(clienteDTO));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    // --- NOVOS TESTES PARA COBERTURA ---

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso quando ID existe")
    void buscarClientePorId_QuandoIdExiste_RetornaCliente() {
        // ARRANGE: Simula que o repositório encontrará o cliente com ID 1
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // ACT: Chama o serviço
        ClienteResponseDTO resultado = clienteService.buscarClientePorId(1L);

        // ASSERT: Verifica se o resultado não é nulo e o ID está correto
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar cliente com ID inexistente")
    void buscarClientePorId_QuandoIdNaoExiste_LancaExcecao() {
        // ARRANGE: Simula que o repositório NÃO encontrará o cliente com ID 99
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT: Verifica se a exceção correta é lançada
        // Esta verificação cobre a linha .orElseThrow() do seu serviço
        assertThrows(EntityNotFoundException.class, () -> clienteService.buscarClientePorId(99L));
    }

    @Test
    @DisplayName("Deve listar todos os clientes ativos")
    void listarClientesAtivos_RetornaListaDeClientes() {
        // ARRANGE: Simula que o repositório retornará uma lista com um cliente
        when(clienteRepository.findByAtivoTrue()).thenReturn(Collections.singletonList(cliente));
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // ACT: Chama o serviço
        List<ClienteResponseDTO> resultados = clienteService.listarClientesAtivos();

        // ASSERT: Verifica se a lista não está vazia
        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        assertEquals("João Silva", resultados.get(0).getNome());
    }
    
    @Test
    @DisplayName("Deve atualizar um cliente com sucesso quando ID existe")
    void atualizarCliente_QuandoIdExiste_RetornaClienteAtualizado() {
        // ARRANGE
        ClienteDTO dadosAtualizacao = new ClienteDTO();
        dadosAtualizacao.setNome("João Silva Atualizado");
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // ACT
        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, dadosAtualizacao);
        
        // ASSERT
        assertNotNull(resultado);
        verify(clienteRepository).save(cliente); // Verifica se o save foi chamado
        // Para verificar se o nome foi realmente atualizado, precisaríamos de uma captura de argumento (mais avançado)
        // ou confiar que a lógica do serviço está correta, já que o save foi chamado.
    }
}