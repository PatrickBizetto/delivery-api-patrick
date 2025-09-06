package com.delivery_api.service.impl;

import com.delivery_api.dto.*;
import com.delivery_api.enums.StatusPedido;
import com.delivery_api.exception.BusinessException;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.model.*;
import com.delivery_api.repository.ClienteRepository;
import com.delivery_api.repository.PedidoRepository;
import com.delivery_api.repository.ProdutoRepository;
import com.delivery_api.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;
    private Pedido pedido;
    private PedidoDTO pedidoDTO;
    private PedidoResponseDTO pedidoResponseDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(true);

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setAtivo(true);
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));

        produto = new Produto();
        produto.setId(10L);
        produto.setNome("Pizza Teste");
        produto.setPreco(new BigDecimal("25.00"));
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);

        ItemPedidoDTO itemDTO = new ItemPedidoDTO();
        itemDTO.setProdutoId(produto.getId());
        itemDTO.setQuantidade(2);

        pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cliente.getId());
        pedidoDTO.setRestauranteId(restaurante.getId());
        pedidoDTO.setItens(Collections.singletonList(itemDTO));
        
        pedido = new Pedido();
        pedido.setId(100L);
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.PENDENTE);
        
        pedidoResponseDTO = new PedidoResponseDTO();
        pedidoResponseDTO.setId(100L);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRestauranteId(1L);
    }

    @Nested
    class CriarPedidoTests {
        @Test
        @DisplayName("Deve criar pedido com sucesso quando todos os dados são válidos")
        void criarPedido_ComDadosValidos_RetornaSucesso() {
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            when(modelMapper.map(any(Pedido.class), eq(PedidoResponseDTO.class))).thenReturn(pedidoResponseDTO);

            PedidoResponseDTO response = pedidoService.criarPedido(pedidoDTO);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(100L);
            verify(pedidoRepository).save(any(Pedido.class));
        }
        
        @Test
        @DisplayName("Deve lançar BusinessException ao criar pedido com cliente inativo")
        void criarPedido_ComClienteInativo_DeveLancarBusinessException() {
            cliente.setAtivo(false);
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Cliente inativo não pode fazer pedidos");
            verify(pedidoRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Deve lançar BusinessException ao criar pedido com restaurante inativo")
        void criarPedido_ComRestauranteInativo_DeveLancarBusinessException() {
            restaurante.setAtivo(false);
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));

            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Restaurante não está disponível");
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando o produto não pertence ao restaurante")
        void criarPedido_ComProdutoDeOutroRestaurante_DeveLancarBusinessException() {
            Restaurante outroRestaurante = new Restaurante();
            outroRestaurante.setId(2L);
            produto.setRestaurante(outroRestaurante);

            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Produto não pertence ao restaurante selecionado");
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando o produto não está disponível")
        void criarPedido_QuandoProdutoIndisponivel_LancaExcecao() {
            produto.setDisponivel(false);
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Produto indisponível: " + produto.getNome());
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o Cliente ID não existe")
        void criarPedido_QuandoClienteNaoExiste_LancaExcecao() {
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTO))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(pedidoRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o Produto ID não existe")
        void criarPedido_QuandoProdutoNaoExiste_LancaExcecao() {
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTO))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(pedidoRepository, never()).save(any());
        }
    }

    @Nested
    class AtualizarStatusPedidoTests {
        @Test
        @DisplayName("Deve atualizar status de PENDENTE para CONFIRMADO")
        void atualizarStatusPedido_DePendenteParaConfirmado_DeveAtualizar() {
            testarTransicaoDeStatusValida(StatusPedido.PENDENTE, StatusPedido.CONFIRMADO);
        }
        
        @Test
        @DisplayName("Deve atualizar status de CONFIRMADO para PREPARANDO")
        void atualizarStatusPedido_DeConfirmadoParaPreparando_DeveAtualizar() {
            testarTransicaoDeStatusValida(StatusPedido.CONFIRMADO, StatusPedido.PREPARANDO);
        }

        @Test
        @DisplayName("Deve atualizar status de PREPARANDO para SAIU_PARA_ENTREGA")
        void atualizarStatusPedido_DePreparandoParaSaiuParaEntrega_DeveAtualizar() {
            testarTransicaoDeStatusValida(StatusPedido.PREPARANDO, StatusPedido.SAIU_PARA_ENTREGA);
        }
        
        @Test
        @DisplayName("Deve atualizar status de SAIU_PARA_ENTREGA para ENTREGUE")
        void atualizarStatusPedido_DeSaiuParaEntregaParaEntregue_DeveAtualizar() {
            testarTransicaoDeStatusValida(StatusPedido.SAIU_PARA_ENTREGA, StatusPedido.ENTREGUE);
        }

        @Test
        @DisplayName("Deve lançar BusinessException com transição inválida de ENTREGUE para PENDENTE")
        void atualizarStatusPedido_DeEntregueParaPendente_DeveLancarExcecao() {
            pedido.setStatus(StatusPedido.ENTREGUE);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.atualizarStatusPedido(100L, StatusPedido.PENDENTE))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Transição de status inválida");
        }

        @Test
        @DisplayName("Deve lançar BusinessException com transição inválida de PENDENTE para ENTREGUE")
        void atualizarStatusPedido_DePendenteParaEntregue_DeveLancarExcecao() {
            pedido.setStatus(StatusPedido.PENDENTE);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.atualizarStatusPedido(100L, StatusPedido.ENTREGUE))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Transição de status inválida");
        }

        @Test
        @DisplayName("Deve lançar BusinessException com transição inválida de CONFIRMADO para ENTREGUE")
        void atualizarStatusPedido_DeConfirmadoParaEntregue_DeveLancarExcecao() {
            pedido.setStatus(StatusPedido.CONFIRMADO);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.atualizarStatusPedido(100L, StatusPedido.ENTREGUE))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Transição de status inválida");
        }

        @Test
        @DisplayName("Deve lançar BusinessException com transição inválida de PREPARANDO para PENDENTE")
        void atualizarStatusPedido_DePreparandoParaPendente_DeveLancarExcecao() {
            pedido.setStatus(StatusPedido.PREPARANDO);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.atualizarStatusPedido(100L, StatusPedido.PENDENTE))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Transição de status inválida");
        }

        @Test
        @DisplayName("Deve lançar BusinessException com transição inválida de SAIU_PARA_ENTREGA para PREPARANDO")
        void atualizarStatusPedido_DeSaiuParaEntregaParaPreparando_DeveLancarExcecao() {
            pedido.setStatus(StatusPedido.SAIU_PARA_ENTREGA);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.atualizarStatusPedido(100L, StatusPedido.PREPARANDO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Transição de status inválida");
        }

        @Test
        @DisplayName("Deve lançar BusinessException com transição inválida de ENTREGUE para CONFIRMADO")
        void atualizarStatusPedido_DeEntregueParaConfirmado_DeveLancarExcecao() {
            pedido.setStatus(StatusPedido.ENTREGUE);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.atualizarStatusPedido(100L, StatusPedido.CONFIRMADO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Transição de status inválida");
        }
    }

    @Nested
    class CancelarPedidoTests {
        @Test
        @DisplayName("Deve cancelar pedido com status CONFIRMADO")
        void cancelarPedido_ComStatusConfirmado_DeveAlterarStatusParaCancelado() {
            pedido.setStatus(StatusPedido.CONFIRMADO);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            pedidoService.cancelarPedido(100L);
            
            assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
            verify(pedidoRepository).save(pedido);
        }

        @Test
        @DisplayName("Deve cancelar pedido com status PENDENTE")
        void cancelarPedido_ComStatusPendente_DeveAlterarStatusParaCancelado() {
            pedido.setStatus(StatusPedido.PENDENTE);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            pedidoService.cancelarPedido(100L);
            
            assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
            verify(pedidoRepository).save(pedido);
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao cancelar pedido com status ENTREGUE")
        void cancelarPedido_ComStatusEntregue_DeveLancarBusinessException() {
            pedido.setStatus(StatusPedido.ENTREGUE);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.cancelarPedido(100L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Pedido não pode ser cancelado");
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao cancelar pedido com status PREPARANDO")
        void cancelarPedido_ComStatusPreparando_DeveLancarBusinessException() {
            pedido.setStatus(StatusPedido.PREPARANDO);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.cancelarPedido(100L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Pedido não pode ser cancelado");
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao cancelar pedido com status SAIU_PARA_ENTREGA")
        void cancelarPedido_ComStatusSaiuParaEntrega_DeveLancarBusinessException() {
            pedido.setStatus(StatusPedido.SAIU_PARA_ENTREGA);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            
            assertThatThrownBy(() -> pedidoService.cancelarPedido(100L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Pedido não pode ser cancelado");
        }
    }

    @Nested
    class CalculoPedidoTests {
        @Test
        @DisplayName("Deve calcular o total de um pedido (versão com lista de itens)")
        void calcularTotalPedido_ComListaDeItens_DeveRetornarSomaCorreta() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
            
            BigDecimal total = pedidoService.calcularTotalPedido(pedidoDTO.getItens());

            assertThat(total).isEqualTo(new BigDecimal("50.00"));
        }
        
        @Test
        @DisplayName("Deve calcular o total de um pedido (versão com DTO de cálculo)")
        void calcularTotalPedido_ComCalculoPedidoDTO_DeveRetornarResponseCorreto() {
            CalculoPedidoDTO calculoDTO = new CalculoPedidoDTO();
            calculoDTO.setRestauranteId(restaurante.getId());
            calculoDTO.setItens(pedidoDTO.getItens());

            when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
            
            CalculoPedidoResponseDTO response = pedidoService.calcularTotalPedido(calculoDTO);

            assertThat(response.getSubtotalItens()).isEqualTo(new BigDecimal("50.00"));
            assertThat(response.getTaxaEntrega()).isEqualTo(new BigDecimal("5.00"));
            assertThat(response.getValorTotal()).isEqualTo(new BigDecimal("55.00"));
        }
    }

    @Nested
    class ListagemPedidosTests {
        @Test
        @DisplayName("Deve listar pedidos com paginação")
        void listarPedidos_DeveRetornarPaginaDeDTOs() {
            Page<Pedido> paginaDePedidos = new PageImpl<>(List.of(pedido));
            when(pedidoRepository.findAll(any(Pageable.class))).thenReturn(paginaDePedidos);
            when(modelMapper.map(any(Pedido.class), eq(PedidoResponseDTO.class))).thenReturn(pedidoResponseDTO);

            Page<PedidoResponseDTO> resultado = pedidoService.listarPedidos(null, null, null, Pageable.unpaged());

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Deve buscar pedidos por restaurante")
        void buscarPedidosPorRestaurante_ComIdValido_DeveRetornarListaDeDTOs() {
            when(pedidoRepository.findByRestauranteId(1L)).thenReturn(List.of(pedido));
            when(modelMapper.map(any(Pedido.class), eq(PedidoResponseDTO.class))).thenReturn(pedidoResponseDTO);

            List<PedidoResponseDTO> resultado = pedidoService.buscarPedidosPorRestaurante(1L, null);

            assertThat(resultado).isNotNull().hasSize(1);
        }

        @Test
        void buscarPedidoPorId_ComIdExistente_DeveRetornarDTO() {
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            when(modelMapper.map(pedido, PedidoResponseDTO.class)).thenReturn(pedidoResponseDTO);
            PedidoResponseDTO response = pedidoService.buscarPedidoPorId(100L);
            assertThat(response).isNotNull();
        }
        
        @Test
        void buscarPedidosPorCliente_ComClienteExistente_DeveRetornarListaDeDTOs() {
            when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido));
            when(modelMapper.map(pedido, PedidoResponseDTO.class)).thenReturn(pedidoResponseDTO);
            List<PedidoResponseDTO> response = pedidoService.buscarPedidosPorCliente(1L);
            assertThat(response).isNotNull().hasSize(1);
        }
    }

    @Nested
    class SecurityTests {
        @Test
        @DisplayName("Deve retornar true se usuário é dono do pedido (cliente)")
        void isClientOwner_WhenUserIsClient_ReturnsTrue() {
            setupSecurityContext(usuario);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

            assertTrue(pedidoService.isClientOwner(100L));
        }

        @Test
        @DisplayName("Deve retornar false se usuário não é dono do pedido")
        void isClientOwner_WhenUserIsNotClient_ReturnsFalse() {
            Usuario outroUsuario = new Usuario();
            outroUsuario.setId(999L);
            setupSecurityContext(outroUsuario);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

            assertFalse(pedidoService.isClientOwner(100L));
        }

        @Test
        @DisplayName("Deve retornar true se usuário é dono do restaurante")
        void isRestaurantOwner_WhenUserIsRestaurantOwner_ReturnsTrue() {
            setupSecurityContext(usuario);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

            assertTrue(pedidoService.isRestaurantOwner(100L));
        }

        @Test
        @DisplayName("Deve retornar false se usuário não é dono do restaurante")
        void isRestaurantOwner_WhenUserIsNotRestaurantOwner_ReturnsFalse() {
            Usuario outroUsuario = new Usuario();
            outroUsuario.setId(999L);
            outroUsuario.setRestauranteId(999L);
            setupSecurityContext(outroUsuario);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

            assertFalse(pedidoService.isRestaurantOwner(100L));
        }

        @Test
        @DisplayName("Deve return true se usuário tem acesso ao pedido")
        void canAccess_WhenUserHasAccess_ReturnsTrue() {
            setupSecurityContext(usuario);
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

            assertTrue(pedidoService.canAccess(100L));
        }

        @Test
        @DisplayName("Deve retornar false se não há usuário autenticado")
        void canAccess_WhenNoAuthentication_ReturnsFalse() {
            SecurityContextHolder.clearContext();
            assertFalse(pedidoService.canAccess(100L));
        }

        private void setupSecurityContext(Usuario usuario) {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(usuario);
            SecurityContextHolder.setContext(securityContext);
        }
    }

    private void testarTransicaoDeStatusValida(StatusPedido statusInicial, StatusPedido statusFinal) {
        pedido.setStatus(statusInicial);
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResponseDTO.class))).thenReturn(pedidoResponseDTO);

        pedidoService.atualizarStatusPedido(100L, statusFinal);
        
        assertThat(pedido.getStatus()).isEqualTo(statusFinal);
        verify(pedidoRepository).save(pedido);
    }
}