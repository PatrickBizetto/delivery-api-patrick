package com.delivery_api.service.impl;

import com.delivery_api.dto.RegisterRequest;
import com.delivery_api.enums.UserRole;
import com.delivery_api.exception.ConflictException;
import com.delivery_api.model.Usuario;
import com.delivery_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("senhaCriptografada");
        usuario.setRole(UserRole.CLIENTE);

        registerRequest = new RegisterRequest();
        registerRequest.setNome("Novo Usuário");
        registerRequest.setEmail("novo@example.com");
        registerRequest.setSenha("senha123");
        registerRequest.setRole(UserRole.CLIENTE);
    }

    // --- Testes para loadUserByUsername ---

    @Test
    @DisplayName("Deve carregar UserDetails com sucesso quando o e-mail existe")
    void loadUserByUsername_ComEmailExistente_DeveRetornarUserDetails() {
        // Arrange
        when(usuarioRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = usuarioService.loadUserByUsername("teste@example.com");

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("teste@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("senhaCriptografada");
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o e-mail não existe")
    void loadUserByUsername_ComEmailInexistente_DeveLancarUsernameNotFoundException() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.loadUserByUsername("inexistente@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado com o email: inexistente@example.com");
    }

    // --- Testes para registrar ---

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void registrar_ComDadosValidos_DeveSalvarERetornarUsuario() {
        // Arrange
        when(usuarioRepository.existsByEmail("novo@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("novaSenhaCriptografada");
        // ArgumentCaptor nos permite "capturar" o objeto que é passado para o método save
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioSalvo = invocation.getArgument(0);
            usuarioSalvo.setId(2L); // Simula o ID gerado pelo banco
            return usuarioSalvo;
        });

        // Act
        Usuario novoUsuario = usuarioService.registrar(registerRequest);

        // Assert
        assertThat(novoUsuario).isNotNull();
        assertThat(novoUsuario.getId()).isEqualTo(2L);
        assertThat(novoUsuario.getEmail()).isEqualTo("novo@example.com");
        assertThat(novoUsuario.getSenha()).isEqualTo("novaSenhaCriptografada");
        assertThat(novoUsuario.isAtivo()).isTrue();

        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar registrar e-mail que já existe")
    void registrar_ComEmailExistente_DeveLancarConflictException() {
        // Arrange
        when(usuarioRepository.existsByEmail("novo@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registrar(registerRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Este email já está em uso.");
        
        // Verifica que os métodos de salvar e codificar a senha nunca foram chamados
        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }
}