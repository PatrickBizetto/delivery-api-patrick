package com.delivery_api.service.impl;

import com.delivery_api.dto.ProdutoDTO;
import com.delivery_api.dto.ProdutoResponseDTO;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.model.Produto;
import com.delivery_api.model.Restaurante;
import com.delivery_api.model.Usuario;
import com.delivery_api.repository.ProdutoRepository;
import com.delivery_api.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceImplTest {

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Restaurante restaurante;
    private Produto produto;
    private ProdutoDTO produtoDTO;
    private ProdutoResponseDTO produtoResponseDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Restaurante Teste");

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Pizza Teste");
        produtoDTO.setDescricao("Descrição da pizza de teste");
        produtoDTO.setPreco(new BigDecimal("50.00"));
        produtoDTO.setCategoria("PIZZA");
        produtoDTO.setRestauranteId(1L);

        produto = new Produto();
        produto.setId(101L);
        produto.setNome("Pizza Teste");
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);

        produtoResponseDTO = new ProdutoResponseDTO();
        produtoResponseDTO.setId(101L);
        produtoResponseDTO.setNome("Pizza Teste");
        produtoResponseDTO.setRestauranteId(1L);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRestauranteId(1L);
    }

    // --- Testes para cadastrarProduto ---

    @Test
    @DisplayName("Deve cadastrar um produto com sucesso quando o restaurante existe")
    void cadastrarProduto_ComRestauranteExistente_DeveRetornarProdutoDTO() {
        // Arrange
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(modelMapper.map(any(ProdutoDTO.class), eq(Produto.class))).thenReturn(produto);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class))).thenReturn(produtoResponseDTO);

        // Act
        ProdutoResponseDTO response = produtoService.cadastrarProduto(produtoDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("Pizza Teste");
        verify(produtoRepository).save(produto);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao cadastrar produto para restaurante inexistente")
    void cadastrarProduto_ComRestauranteInexistente_DeveLancarEntityNotFoundException() {
        // Arrange
        when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.cadastrarProduto(produtoDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Restaurante não encontrado: 1");
        
        verify(produtoRepository, never()).save(any());
    }

    // --- Testes para buscarProdutoPorId ---

    @Test
    @DisplayName("Deve retornar um produto quando o ID existe")
    void buscarProdutoPorId_ComIdExistente_DeveRetornarProdutoDTO() {
        // Arrange
        when(produtoRepository.findById(101L)).thenReturn(Optional.of(produto));
        when(modelMapper.map(produto, ProdutoResponseDTO.class)).thenReturn(produtoResponseDTO);

        // Act
        ProdutoResponseDTO response = produtoService.buscarProdutoPorId(101L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar produto com ID inexistente")
    void buscarProdutoPorId_ComIdInexistente_DeveLancarEntityNotFoundException() {
        // Arrange
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.buscarProdutoPorId(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // --- Testes para atualizarProduto ---

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void atualizarProduto_ComIdExistente_DeveRetornarProdutoAtualizadoDTO() {
        // Arrange
        when(produtoRepository.findById(101L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(modelMapper.map(produto, ProdutoResponseDTO.class)).thenReturn(produtoResponseDTO);
        
        // Act
        ProdutoResponseDTO response = produtoService.atualizarProduto(101L, produtoDTO);

        // Assert
        assertThat(response).isNotNull();
        verify(produtoRepository).save(produto);
        assertThat(produto.getNome()).isEqualTo(produtoDTO.getNome());
        assertThat(produto.getDescricao()).isEqualTo(produtoDTO.getDescricao());
    }

    // --- Testes para removerProduto ---

    @Test
    @DisplayName("Deve remover um produto com sucesso")
    void removerProduto_ComIdExistente_DeveChamarDeleteById() {
        // Arrange
        when(produtoRepository.existsById(101L)).thenReturn(true);
        doNothing().when(produtoRepository).deleteById(101L);

        // Act
        produtoService.removerProduto(101L);

        // Assert
        verify(produtoRepository, times(1)).deleteById(101L);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar remover produto com ID inexistente")
    void removerProduto_ComIdInexistente_DeveLancarEntityNotFoundException() {
        // Arrange
        when(produtoRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> produtoService.removerProduto(99L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(produtoRepository, never()).deleteById(anyLong());
    }
    
    // --- Testes para alterarDisponibilidade ---
    
    @Test
    @DisplayName("Deve alterar a disponibilidade de um produto")
    void alterarDisponibilidade_ComIdExistente_DeveInverterOStatus() {
        // Arrange
        produto.setDisponivel(true); // Estado inicial
        when(produtoRepository.findById(101L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(modelMapper.map(produto, ProdutoResponseDTO.class)).thenReturn(produtoResponseDTO);

        // Act
        ProdutoResponseDTO response = produtoService.alterarDisponibilidade(101L);

        // Assert
        assertThat(produto.isDisponivel()).isFalse(); // Verifica se o estado foi invertido
        verify(produtoRepository).save(produto);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("alterarDisponibilidade deve lançar exceção quando produto não existe")
    void alterarDisponibilidade_ProdutoInexistente_DeveLancarExcecao() {
        // Arrange
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.alterarDisponibilidade(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // --- Testes para buscarProdutosPorRestaurante ---

    @Test
    @DisplayName("Deve buscar produtos por restaurante filtrando por disponíveis")
    void buscarProdutosPorRestaurante_ComDisponivelTrue_DeveChamarMetodoCorreto() {
        // Arrange
        when(produtoRepository.findByRestauranteIdAndDisponivelTrue(1L)).thenReturn(List.of(produto));
        when(modelMapper.map(produto, ProdutoResponseDTO.class)).thenReturn(produtoResponseDTO);

        // Act
        List<ProdutoResponseDTO> result = produtoService.buscarProdutosPorRestaurante(1L, true);

        // Assert
        assertThat(result).hasSize(1);
        verify(produtoRepository).findByRestauranteIdAndDisponivelTrue(1L);
        verify(produtoRepository, never()).findByRestauranteId(anyLong());
    }
    
    @Test
    @DisplayName("Deve buscar todos os produtos por restaurante quando disponível é nulo")
    void buscarProdutosPorRestaurante_ComDisponivelNull_DeveChamarMetodoCorreto() {
        // Arrange
        when(produtoRepository.findByRestauranteId(1L)).thenReturn(List.of(produto));
        when(modelMapper.map(produto, ProdutoResponseDTO.class)).thenReturn(produtoResponseDTO);

        // Act
        List<ProdutoResponseDTO> result = produtoService.buscarProdutosPorRestaurante(1L, null);

        // Assert
        assertThat(result).hasSize(1);
        verify(produtoRepository).findByRestauranteId(1L);
        verify(produtoRepository, never()).findByRestauranteIdAndDisponivelTrue(anyLong());
    }
    
    @Test
    @DisplayName("Deve buscar todos os produtos por restaurante quando disponível é false")
    void buscarProdutosPorRestaurante_ComDisponivelFalse_DeveChamarMetodoCorreto() {
        // Arrange
        when(produtoRepository.findByRestauranteId(1L)).thenReturn(List.of(produto));
        when(modelMapper.map(produto, ProdutoResponseDTO.class)).thenReturn(produtoResponseDTO);

        // Act
        List<ProdutoResponseDTO> result = produtoService.buscarProdutosPorRestaurante(1L, false);

        // Assert
        assertThat(result).hasSize(1);
        verify(produtoRepository).findByRestauranteId(1L);
        verify(produtoRepository, never()).findByRestauranteIdAndDisponivelTrue(anyLong());
    }

    // --- Testes para Listar e Buscar ---

    @Test
    @DisplayName("Deve listar todos os produtos")
    void listarTodosProdutos_DeveRetornarListaDeDTOs() {
        // Arrange
        when(produtoRepository.findAll()).thenReturn(List.of(produto));
        when(modelMapper.map(produto, ProdutoResponseDTO.class)).thenReturn(produtoResponseDTO);
        
        // Act
        List<ProdutoResponseDTO> result = produtoService.listarTodosProdutos();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Pizza Teste");
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria")
    void buscarProdutosPorCategoria_DeveRetornarLista() {
        // Arrange
        when(produtoRepository.findByCategoriaAndDisponivelTrue("PIZZA"))
            .thenReturn(List.of(produto));
        when(modelMapper.map(produto, ProdutoResponseDTO.class))
            .thenReturn(produtoResponseDTO);

        // Act
        List<ProdutoResponseDTO> result = produtoService.buscarProdutosPorCategoria("PIZZA");
        
        // Assert
        assertThat(result).hasSize(1);
        verify(produtoRepository).findByCategoriaAndDisponivelTrue("PIZZA");
    }

    @Test
    @DisplayName("Deve buscar produtos por nome")
    void buscarProdutosPorNome_DeveRetornarLista() {
        // Arrange
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("Pizza"))
            .thenReturn(List.of(produto));
        when(modelMapper.map(produto, ProdutoResponseDTO.class))
            .thenReturn(produtoResponseDTO);

        // Act
        List<ProdutoResponseDTO> result = produtoService.buscarProdutosPorNome("Pizza");
        
        // Assert
        assertThat(result).hasSize(1);
        verify(produtoRepository).findByNomeContainingIgnoreCaseAndDisponivelTrue("Pizza");
    }

    // --- Testes para isOwner ---

    @Test
    @DisplayName("isOwner deve retornar false quando não há autenticação")
    void isOwner_SemAutenticacao_DeveRetornarFalse() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = produtoService.isOwner(1L);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isOwner deve retornar false quando usuário não tem restaurante")
    void isOwner_UsuarioSemRestaurante_DeveRetornarFalse() {
        // Arrange
        usuario.setRestauranteId(null);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = produtoService.isOwner(1L);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isOwner deve retornar true quando usuário é dono do produto")
    void isOwner_UsuarioDono_DeveRetornarTrue() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        SecurityContextHolder.setContext(securityContext);
        
        when(produtoRepository.findById(101L)).thenReturn(Optional.of(produto));

        // Act
        boolean result = produtoService.isOwner(101L);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isOwner deve lançar exceção quando produto não existe")
    void isOwner_ProdutoInexistente_DeveLancarExcecao() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        SecurityContextHolder.setContext(securityContext);
        
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.isOwner(999L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("isOwner deve retornar false quando principal não é Usuario")
    void isOwner_PrincipalNaoEUsuario_DeveRetornarFalse() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not_a_user_object");
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = produtoService.isOwner(1L);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isOwner deve retornar false quando autenticação é nula")
    void isOwner_AutenticacaoNula_DeveRetornarFalse() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = produtoService.isOwner(1L);

        // Assert
        assertThat(result).isFalse();
    }
}