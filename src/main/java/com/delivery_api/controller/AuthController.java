package com.delivery_api.controller;

import com.delivery_api.dto.LoginRequest;
import com.delivery_api.dto.LoginResponse;
import com.delivery_api.dto.RegisterRequest;
import com.delivery_api.dto.UserResponse;
import com.delivery_api.model.Usuario;
import com.delivery_api.security.JwtUtil;
import com.delivery_api.service.impl.UsuarioServiceImpl;

// 🔹 IMPORTS DO SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
// 🔹 ANOTAÇÃO PARA AGRUPAR OS ENDPOINTS
@Tag(name = "Autenticação", description = "Endpoints para realizar login, registro e obter dados do usuário autenticado.")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔹 DOCUMENTAÇÃO DO ENDPOINT DE LOGIN
    @Operation(summary = "Realiza o login do usuário",
               description = "Autentica um usuário com base no e-mail e senha e retorna um token JWT válido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token retornado"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        
        Usuario usuario = (Usuario) userDetails;
        UserResponse userResponse = new UserResponse(usuario);

        return ResponseEntity.ok(new LoginResponse(token, "Bearer", userResponse));
    }

    // 🔹 DOCUMENTAÇÃO DO ENDPOINT DE REGISTRO
    @Operation(summary = "Registra um novo usuário",
               description = "Cria um novo usuário no sistema. Requer nome, e-mail e senha.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflito - E-mail já cadastrado") // Exemplo de erro comum
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Usuario usuario = usuarioService.registrar(registerRequest);
        return new ResponseEntity<>(new UserResponse(usuario), HttpStatus.CREATED);
    }

    // 🔹 DOCUMENTAÇÃO DO ENDPOINT 'ME' (PROTEGIDO)
    @Operation(summary = "Obtém dados do usuário logado",
               description = "Retorna as informações do usuário correspondente ao token JWT enviado no cabeçalho de autorização.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados do usuário retornados"),
        @ApiResponse(responseCode = "401", description = "Não autorizado - Token inválido ou expirado")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 INDICA QUE ESTE ENDPOINT É PROTEGIDO
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(principal.getName());
        return ResponseEntity.ok(new UserResponse(usuario));
    }
}