package com.delivery_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delivery_api.config.SecurityConfigTest;
import com.delivery_api.dto.LoginRequest;
import com.delivery_api.dto.RegisterRequest;
import com.delivery_api.enums.UserRole;
import com.delivery_api.exception.ConflictException;
import com.delivery_api.model.Usuario;
import com.delivery_api.security.CustomAccessDeniedHandler;
import com.delivery_api.security.CustomAuthenticationEntryPoint;
import com.delivery_api.security.JwtAuthenticationFilter;
import com.delivery_api.security.JwtUtil;
import com.delivery_api.service.impl.UsuarioServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfigTest.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsuarioServiceImpl usuarioService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @MockBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Test
    void deveRetornarToken_QuandoLoginComCredenciaisValidas() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("teste@email.com");
        loginRequest.setSenha("senha123");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senhaCriptografada");
        usuario.setRole(UserRole.CLIENTE);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(usuario, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(usuarioService.loadUserByUsername("teste@email.com")).thenReturn(usuario);
        when(jwtUtil.generateToken(usuario)).thenReturn("mock.jwt.token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.user.nome").value("Usuário Teste"));
        }

    @Test
    void deveRetornarUnauthorized_QuandoLoginComCredenciaisInvalidas() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("errado@email.com");
        loginRequest.setSenha("senhaErrada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornarCreated_QuandoRegistrarNovoUsuario() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNome("Novo Usuário");
        registerRequest.setEmail("novo@email.com");
        registerRequest.setSenha("senhaNova");
        registerRequest.setRole(UserRole.CLIENTE);

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(2L);
        usuarioSalvo.setNome("Novo Usuário");
        usuarioSalvo.setEmail("novo@email.com");
        usuarioSalvo.setSenha("senhaCriptografada");
        usuarioSalvo.setRole(UserRole.CLIENTE);

        when(usuarioService.registrar(any(RegisterRequest.class))).thenReturn(usuarioSalvo);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.email").value("novo@email.com"));
    }

    @Test
    void deveRetornarConflict_QuandoRegistrarComEmailJaExistente() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNome("Outro Usuário");
        registerRequest.setEmail("existente@email.com");
        registerRequest.setSenha("outraSenha");
        registerRequest.setRole(UserRole.CLIENTE);

        when(usuarioService.registrar(any(RegisterRequest.class)))
                .thenThrow(new ConflictException("Este email já está em uso."));
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "logado@email.com")
    void deveRetornarDadosDoUsuario_QuandoUsuarioEstaAutenticado() throws Exception {
        // Arrange
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(3L);
        usuarioLogado.setNome("Usuário Logado");
        usuarioLogado.setEmail("logado@email.com");
        usuarioLogado.setSenha("senhaCripto");
        usuarioLogado.setRole(UserRole.CLIENTE);

        when(usuarioService.loadUserByUsername("logado@email.com")).thenReturn(usuarioLogado);

        // Act & Assert
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.nome").value("Usuário Logado"))
                .andExpect(jsonPath("$.email").value("logado@email.com"));
    }
}