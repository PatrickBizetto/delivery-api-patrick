package com.delivery_api.service.impl;

import com.delivery_api.dto.RestauranteDTO;
import com.delivery_api.dto.RestauranteResponseDTO;
import com.delivery_api.exception.ConflictException;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.model.Restaurante;
import com.delivery_api.model.Usuario;
import com.delivery_api.repository.RestauranteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestauranteServiceImplTest {

    @InjectMocks
    private RestauranteServiceImpl restauranteService;

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ModelMapper modelMapper;

    private Restaurante restaurante;
    private RestauranteDTO restauranteDTO;
    private RestauranteResponseDTO restauranteResponseDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        restauranteDTO = new RestauranteDTO();
        restauranteDTO.setNome("Pizza Place");
        restauranteDTO.setTelefone("11999998888");
        restauranteDTO.setTaxaEntrega(new BigDecimal("5.00"));

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Pizza Place");
        restaurante.setTelefone("11999998888");
        restaurante.setAtivo(true);

        restauranteResponseDTO = new RestauranteResponseDTO();
        restauranteResponseDTO.setId(1L);
        restauranteResponseDTO.setNome("Pizza Place");
        
        usuario = new Usuario();
        usuario.setRestauranteId(1L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve cadastrar um restaurante com sucesso")
    void cadastrarRestaurante_ComDadosValidos_DeveRetornarRestauranteDTO() {
        when(restauranteRepository.existsByTelefone(anyString())).thenReturn(false);
        when(modelMapper.map(any(RestauranteDTO.class), eq(Restaurante.class))).thenReturn(restaurante);
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class))).thenReturn(restauranteResponseDTO);

        RestauranteResponseDTO response = restauranteService.cadastrarRestaurante(restauranteDTO);

        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("Pizza Place");
        verify(restauranteRepository).save(any(Restaurante.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar cadastrar telefone que já existe")
    void cadastrarRestaurante_ComTelefoneExistente_DeveLancarConflictException() {
        when(restauranteRepository.existsByTelefone(anyString())).thenReturn(true);

        assertThatThrownBy(() -> restauranteService.cadastrarRestaurante(restauranteDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Telefone já cadastrado no sistema.");

        verify(restauranteRepository, never()).save(any(Restaurante.class));
    }

    @Test
    @DisplayName("Deve retornar uma página de restaurantes")
    void listarRestaurantes_QuandoChamado_DeveRetornarPaginaDeDTOs() {
        Page<Restaurante> paginaDeRestaurantes = new PageImpl<>(Collections.singletonList(restaurante));
        when(restauranteRepository.findAll(any(Pageable.class))).thenReturn(paginaDeRestaurantes);
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class))).thenReturn(restauranteResponseDTO);

        Page<RestauranteResponseDTO> resultado = restauranteService.listarRestaurantes(null, null, Pageable.unpaged());

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Pizza Place");
    }

    @Test
    @DisplayName("Deve retornar um restaurante quando o ID existe")
    void buscarRestaurantePorId_ComIdExistente_DeveRetornarRestauranteDTO() {
        when(restauranteRepository.findById(anyLong())).thenReturn(Optional.of(restaurante));
        when(modelMapper.map(restaurante, RestauranteResponseDTO.class)).thenReturn(restauranteResponseDTO);

        RestauranteResponseDTO response = restauranteService.buscarRestaurantePorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando o ID não existe")
    void buscarRestaurantePorId_ComIdInexistente_DeveLancarEntityNotFoundException() {
        when(restauranteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restauranteService.buscarRestaurantePorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Restaurante", "ID 99", "não foi encontrado");
    }

    @Test
    @DisplayName("Deve atualizar um restaurante com sucesso")
    void atualizarRestaurante_ComIdExistente_DeveRetornarRestauranteAtualizadoDTO() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(modelMapper.map(restaurante, RestauranteResponseDTO.class)).thenReturn(restauranteResponseDTO);
        
        doNothing().when(modelMapper).map(any(RestauranteDTO.class), any(Restaurante.class));

        RestauranteResponseDTO response = restauranteService.atualizarRestaurante(1L, restauranteDTO);

        assertThat(response).isNotNull();
        verify(modelMapper).map(restauranteDTO, restaurante); 
        verify(restauranteRepository).save(restaurante);
    }

    @Test
    @DisplayName("Deve alterar o status de um restaurante de ativo para inativo")
    void alterarStatusRestaurante_DeAtivoParaInativo_DeveRetornarComStatusAlterado() {
        restaurante.setAtivo(true);
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class))).thenReturn(restauranteResponseDTO);

        restauranteService.alterarStatusRestaurante(1L);

        assertThat(restaurante.isAtivo()).isFalse();
        verify(restauranteRepository).save(restaurante);
    }

    @Test
    @DisplayName("Deve alterar status de inativo para ativo")
    void alterarStatusRestaurante_DeInativoParaAtivo_DeveMudarStatus() {
        restaurante.setAtivo(false);
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class))).thenReturn(restauranteResponseDTO);
        
        restauranteService.alterarStatusRestaurante(1L);
        
        assertThat(restaurante.isAtivo()).isTrue();
        verify(restauranteRepository).save(restaurante);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar status de restaurante inexistente")
    void alterarStatusRestaurante_RestauranteInexistente_DeveLancarExcecao() {
        when(restauranteRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> restauranteService.alterarStatusRestaurante(99L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve deletar um restaurante com sucesso")
    void deletarRestaurante_ComIdExistente_DeveChamarDeleteById() {
        when(restauranteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(restauranteRepository).deleteById(1L);

        restauranteService.deletarRestaurante(1L);

        verify(restauranteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar deletar ID que não existe")
    void deletarRestaurante_ComIdInexistente_DeveLancarEntityNotFoundException() {
        when(restauranteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> restauranteService.deletarRestaurante(99L))
                .isInstanceOf(EntityNotFoundException.class);
        
        verify(restauranteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve calcular taxa de entrega para restaurante existente")
    void calcularTaxaEntrega_ComIdExistente_DeveRetornarValorFixo() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        BigDecimal taxa = restauranteService.calcularTaxaEntrega(1L, "12345-678");
        
        assertThat(taxa).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular taxa para restaurante inexistente")
    void calcularTaxaEntrega_RestauranteInexistente_DeveLancarExcecao() {
        when(restauranteRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> restauranteService.calcularTaxaEntrega(99L, "12345"))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve buscar restaurantes por categoria com sucesso")
    void buscarRestaurantesPorCategoria_ComCategoriaExistente_DeveRetornarListaDeDTOs() {
        when(restauranteRepository.findByCategoriaAndAtivoTrue("ITALIANA")).thenReturn(List.of(restaurante));
        when(modelMapper.map(restaurante, RestauranteResponseDTO.class)).thenReturn(restauranteResponseDTO);

        List<RestauranteResponseDTO> resultado = restauranteService.buscarRestaurantesPorCategoria("ITALIANA");

        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getNome()).isEqualTo("Pizza Place");
    }

    @Test
    @DisplayName("Deve retornar lista vazia para restaurantes próximos (não implementado)")
    void buscarRestaurantesProximos_NaoImplementado_DeveRetornarVazio() {
        List<RestauranteResponseDTO> resultado = restauranteService.buscarRestaurantesProximos("12345678", 10);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar true quando usuário é dono do restaurante")
    void isOwner_QuandoUsuarioEDono_DeveRetornarTrue() {
        // Configurar contexto de segurança
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(authentication.isAuthenticated()).thenReturn(true);

        boolean resultado = restauranteService.isOwner(1L);
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não é dono")
    void isOwner_QuandoUsuarioNaoEDono_DeveRetornarFalse() {
        usuario.setRestauranteId(999L); // ID diferente
        
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(authentication.isAuthenticated()).thenReturn(true);

        boolean resultado = restauranteService.isOwner(1L);
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando não há autenticação")
    void isOwner_QuandoNaoAutenticado_DeveRetornarFalse() {
        SecurityContextHolder.clearContext();

        boolean resultado = restauranteService.isOwner(1L);
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando o principal não é uma instância de Usuario")
    void isOwner_QuandoPrincipalNaoEUsuario_DeveRetornarFalse() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.getPrincipal()).thenReturn("not_a_user_object");
        when(authentication.isAuthenticated()).thenReturn(true);

        boolean resultado = restauranteService.isOwner(1L);
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não tem restauranteId")
    void isOwner_QuandoUsuarioSemRestauranteId_DeveRetornarFalse() {
        usuario.setRestauranteId(null);
        
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(authentication.isAuthenticated()).thenReturn(true);

        boolean resultado = restauranteService.isOwner(1L);
        assertThat(resultado).isFalse();
    }
}