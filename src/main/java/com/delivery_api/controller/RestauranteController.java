package com.delivery_api.controller;

import com.delivery_api.dto.*;
import com.delivery_api.service.ProdutoService;
import com.delivery_api.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- ADICIONE ESTE IMPORT
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Operações relacionadas aos restaurantes")
@Validated
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;
    
    @Autowired
    private ProdutoService produtoService;

    // REGRA: Apenas usuários com perfil ADMIN podem cadastrar restaurantes.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar restaurante (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Restaurante já existe")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> cadastrar(
            @Valid @RequestBody RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.cadastrarRestaurante(dto);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // REGRA: Endpoint público. Acesso liberado para todos no SecurityConfig.
    @GetMapping
    @Operation(summary = "Listar restaurantes (Público)")
    public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo,
            Pageable pageable) {
        Page<RestauranteResponseDTO> restaurantes =
                restauranteService.listarRestaurantes(categoria, ativo, pageable);
        PagedResponseWrapper<RestauranteResponseDTO> response =
                new PagedResponseWrapper<>(restaurantes);
        return ResponseEntity.ok(response);
    }
    
    // REGRA: Endpoint público. Acesso liberado para todos no SecurityConfig.
    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID (Público)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> buscarPorId(
            @Parameter(description = "ID do restaurante a ser buscado")
            @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id) {
        RestauranteResponseDTO restaurante = restauranteService.buscarRestaurantePorId(id);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante encontrado");
        return ResponseEntity.ok(response);
    }

    // REGRA: Apenas ADMIN ou o DONO do restaurante pode atualizar.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteServiceImpl.isOwner(#id))")
    @Operation(summary = "Atualizar restaurante (Admin ou Dono)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> atualizar(
            @Parameter(description = "ID do restaurante a ser atualizado")
            @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id,
            @Valid @RequestBody RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.atualizarRestaurante(id, dto);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante atualizado com sucesso");
        return ResponseEntity.ok(response);
    }
    
    // REGRA: Apenas ADMIN pode deletar um restaurante.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar restaurante (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurante deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do restaurante a ser deletado")
            @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id) {
        restauranteService.deletarRestaurante(id);
        return ResponseEntity.noContent().build();
    }

    // --- Outros Endpoints ---
    
    // REGRA: Apenas ADMIN ou o DONO do restaurante pode alterar o status.
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteServiceImpl.isOwner(#id))")
    @Operation(summary = "Ativar/Desativar restaurante (Admin ou Dono)")
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> alterarStatus(
            @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id) {
        RestauranteResponseDTO restaurante = restauranteService.alterarStatusRestaurante(id);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Status alterado com sucesso");
        return ResponseEntity.ok(response);
    }
    
    // REGRA: Endpoint público para ver o cardápio de um restaurante.
    @GetMapping("/{restauranteId}/produtos")
    @Operation(summary = "Listar produtos de um restaurante (Público)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> buscarProdutosDoRestaurante(
            @PathVariable @Positive(message = "O ID do restaurante deve ser um número positivo") Long restauranteId,
            @RequestParam(required = false) Boolean disponivel) {
        List<ProdutoResponseDTO> produtos =
                produtoService.buscarProdutosPorRestaurante(restauranteId, disponivel);
        ApiResponseWrapper<List<ProdutoResponseDTO>> response =
                new ApiResponseWrapper<>(true, produtos, "Produtos encontrados");
        return ResponseEntity.ok(response);
    }
}