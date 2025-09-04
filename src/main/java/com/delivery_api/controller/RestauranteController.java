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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Operações relacionadas aos restaurantes")
@Validated // Habilita a validação para parâmetros de métodos, como @Positive no @PathVariable
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;
    
    // A injeção do ProdutoService deve ficar aqui, apenas uma vez.
    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @Operation(summary = "Cadastrar restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Restaurante já existe")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> cadastrar(
            @Valid @RequestBody RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.cadastrarRestaurante(dto);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar restaurantes")
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

    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID")
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

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
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
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurante deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do restaurante a ser deletado")
            @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id) {
        restauranteService.deletarRestaurante(id);
        return ResponseEntity.noContent().build();
    }

    // --- Outros Endpoints ---
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar/Desativar restaurante")
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> alterarStatus(
            @PathVariable @Positive(message = "O ID deve ser um número positivo") Long id) {
        RestauranteResponseDTO restaurante = restauranteService.alterarStatusRestaurante(id);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Status alterado com sucesso");
        return ResponseEntity.ok(response);
    }
    
    // O método de buscar produtos deve existir apenas uma vez.
    @GetMapping("/{restauranteId}/produtos")
    @Operation(summary = "Listar produtos de um restaurante")
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