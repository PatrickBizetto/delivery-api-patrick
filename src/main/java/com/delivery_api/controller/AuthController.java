package com.delivery_api.controller;

import com.delivery_api.dto.LoginRequest;
import com.delivery_api.dto.LoginResponse;
import com.delivery_api.dto.RegisterRequest;
import com.delivery_api.dto.UserResponse;
import com.delivery_api.model.Usuario;
import com.delivery_api.security.JwtUtil;
import com.delivery_api.service.impl.UsuarioServiceImpl;
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
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

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

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Usuario usuario = usuarioService.registrar(registerRequest);
        return new ResponseEntity<>(new UserResponse(usuario), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
        // O Principal é injetado pelo Spring Security após a validação do token
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(principal.getName());
        return ResponseEntity.ok(new UserResponse(usuario));
    }
}