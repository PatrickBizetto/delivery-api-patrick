package com.delivery_api.controller;

import com.delivery_api.dto.*;
import com.delivery_api.service.ProdutoService;
import com.delivery_api.service.RestauranteService;
// 🔹 IMPORTS DO SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Operações para consultar e gerenciar restaurantes.")
@Validated
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;
    
    @Autowired
    private ProdutoService produtoService;

    // 🔹 ENDPOINT PROTEGIDO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar um novo restaurante (Admin)",
               description = "Cria um novo restaurante no sistema. Requer permissão de Administrador.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "409", description = "Conflito - Restaurante com este CNPJ ou e-mail já existe")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 REQUER AUTENTICAÇÃO
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> cadastrar(@Valid @RequestBody RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.cadastrarRestaurante(dto);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔹 ENDPOINT PÚBLICO
    @GetMapping
    @Operation(summary = "Listar restaurantes (Público)",
               description = "Retorna uma lista paginada de restaurantes, com filtros opcionais por categoria e status.")
    @ApiResponses({ // 🔹 ADICIONADO
        @ApiResponse(responseCode = "200", description = "Lista de restaurantes retornada com sucesso")
    })
    public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> listar(
            @Parameter(description = "Filtrar por categoria de cozinha") @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar por restaurantes ativos (true) ou inativos (false)") @RequestParam(required = false) Boolean ativo,
            Pageable pageable) {
        Page<RestauranteResponseDTO> restaurantes =
                restauranteService.listarRestaurantes(categoria, ativo, pageable);
        PagedResponseWrapper<RestauranteResponseDTO> response =
                new PagedResponseWrapper<>(restaurantes);
        return ResponseEntity.ok(response);
    }
    
    // 🔹 ENDPOINT PÚBLICO
    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID (Público)",
               description = "Recupera os detalhes de um restaurante específico pelo seu ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> buscarPorId(@Parameter(description = "ID do restaurante a ser buscado") @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id) {
        RestauranteResponseDTO restaurante = restauranteService.buscarRestaurantePorId(id);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante encontrado");
        return ResponseEntity.ok(response);
    }

    // 🔹 ENDPOINT PROTEGIDO
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteServiceImpl.isOwner(#id))")
    @Operation(summary = "Atualizar restaurante (Admin ou Dono)",
               description = "Atualiza os dados de um restaurante existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "401", description = "Não autorizado"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 REQUER AUTENTICAÇÃO
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> atualizar(
            @Parameter(description = "ID do restaurante a ser atualizado") @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id,
            @Valid @RequestBody RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.atualizarRestaurante(id, dto);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante atualizado com sucesso");
        return ResponseEntity.ok(response);
    }
    
    // 🔹 ENDPOINT PROTEGIDO
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar restaurante (Admin)",
               description = "Remove um restaurante do sistema. Esta é uma operação destrutiva.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Restaurante deletado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 REQUER AUTENTICAÇÃO
    public ResponseEntity<Void> deletar(@Parameter(description = "ID do restaurante a ser deletado") @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id) {
        restauranteService.deletarRestaurante(id);
        return ResponseEntity.noContent().build();
    }
    
    // 🔹 ENDPOINT PROTEGIDO
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteServiceImpl.isOwner(#id))")
    @Operation(summary = "Ativar ou desativar um restaurante (Admin ou Dono)",
               description = "Alterna o status de um restaurante entre ativo e inativo.")
    @ApiResponses({ // 🔹 ADICIONADO
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 REQUER AUTENTICAÇÃO
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> alterarStatus(@Parameter(description = "ID do restaurante") @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id) {
        RestauranteResponseDTO restaurante = restauranteService.alterarStatusRestaurante(id);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Status alterado com sucesso");
        return ResponseEntity.ok(response);
    }
    
    // 🔹 ENDPOINT PÚBLICO
    @GetMapping("/{restauranteId}/produtos")
    @Operation(summary = "Listar produtos de um restaurante (Público)",
               description = "Retorna o cardápio (lista de produtos) de um restaurante específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> buscarProdutosDoRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable @Positive(message = "O ID do restaurante deve ser um número positivo") Long restauranteId,
            @Parameter(description = "Filtrar por produtos disponíveis (true) ou todos (false/omitido)") @RequestParam(required = false) Boolean disponivel) {
        List<ProdutoResponseDTO> produtos =
                produtoService.buscarProdutosPorRestaurante(restauranteId, disponivel);
        ApiResponseWrapper<List<ProdutoResponseDTO>> response =
                new ApiResponseWrapper<>(true, produtos, "Produtos encontrados");
        return ResponseEntity.ok(response);
    }
}