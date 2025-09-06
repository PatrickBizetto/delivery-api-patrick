package com.delivery_api.controller;

import com.delivery_api.dto.*;
import com.delivery_api.service.ProdutoService;
// 🔹 IMPORTS DO SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "Operações para consultar e gerenciar produtos.")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // 🔹 ENDPOINT PROTEGIDO
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @Operation(summary = "Cadastrar um novo produto (Restaurante ou Admin)",
               description = "Cria um novo produto associado a um restaurante.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 REQUER AUTENTICAÇÃO
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> cadastrar(@Valid @RequestBody ProdutoDTO dto) {
        ProdutoResponseDTO produto = produtoService.cadastrarProduto(dto);
        ApiResponseWrapper<ProdutoResponseDTO> response =
                new ApiResponseWrapper<>(true, produto, "Produto criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔹 ENDPOINT PÚBLICO
    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID (Público)",
               description = "Recupera os detalhes de um produto específico pelo seu ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> buscarPorId(@Parameter(description = "ID do produto") @PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        ApiResponseWrapper<ProdutoResponseDTO> response =
                new ApiResponseWrapper<>(true, produto, "Produto encontrado");
        return ResponseEntity.ok(response);
    }

    // 🔹 ENDPOINT PROTEGIDO
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoServiceImpl.isOwner(#id)")
    @Operation(summary = "Atualizar produto (Admin ou Dono)",
               description = "Atualiza os dados de um produto existente. O dono é o restaurante associado ao produto.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "401", description = "Não autorizado"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 REQUER AUTENTICAÇÃO
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> atualizar(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody ProdutoDTO dto) {
        ProdutoResponseDTO produto = produtoService.atualizarProduto(id, dto);
        ApiResponseWrapper<ProdutoResponseDTO> response =
                new ApiResponseWrapper<>(true, produto, "Produto atualizado com sucesso");
        return ResponseEntity.ok(response);
    }

    // 🔹 ENDPOINT PROTEGIDO
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoServiceImpl.isOwner(#id)")
    @Operation(summary = "Remover produto (Admin ou Dono)",
               description = "Remove um produto do sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 REQUER AUTENTICAÇÃO
    public ResponseEntity<Void> remover(@Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 ENDPOINT PROTEGIDO
    @PatchMapping("/{id}/disponibilidade")
    @PreAuthorize("hasRole('ADMIN') or @produtoServiceImpl.isOwner(#id)")
    @Operation(summary = "Alterar disponibilidade do produto (Admin ou Dono)",
               description = "Alterna o status de disponibilidade de um produto (disponível/indisponível).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Disponibilidade alterada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"), // 🔹 ADICIONADO
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth") // 🔹 REQUER AUTENTICAÇÃO
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> alterarDisponibilidade(@Parameter(description = "ID do produto") @PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.alterarDisponibilidade(id);
        ApiResponseWrapper<ProdutoResponseDTO> response =
                new ApiResponseWrapper<>(true, produto, "Disponibilidade alterada com sucesso");
        return ResponseEntity.ok(response);
    }

    // 🔹 ENDPOINT PÚBLICO
    @GetMapping
    @Operation(summary = "Listar todos os produtos (Público)",
               description = "Retorna uma lista de todos os produtos disponíveis de todos os restaurantes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodosProdutos();
        ApiResponseWrapper<List<ProdutoResponseDTO>> response =
                new ApiResponseWrapper<>(true, produtos, "Lista de produtos");
        return ResponseEntity.ok(response);
    }

    // 🔹 ENDPOINT PÚBLICO
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar produtos por categoria (Público)",
               description = "Lista produtos disponíveis de uma categoria específica.")
    @ApiResponses({ // 🔹 ADICIONADO
        @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso")
    })
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> buscarPorCategoria(@Parameter(description = "Nome da categoria do produto") @PathVariable String categoria) {
        List<ProdutoResponseDTO> produtos =
                produtoService.buscarProdutosPorCategoria(categoria);
        ApiResponseWrapper<List<ProdutoResponseDTO>> response =
                new ApiResponseWrapper<>(true, produtos, "Produtos encontrados");
        return ResponseEntity.ok(response);
    }

    // 🔹 ENDPOINT PÚBLICO
    @GetMapping("/buscar")
    @Operation(summary = "Buscar produtos por nome (Público)",
               description = "Busca produtos cujo nome contenha o termo pesquisado.")
    @ApiResponses({ // 🔹 ADICIONADO
        @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> buscarPorNome(@Parameter(description = "Termo para buscar no nome do produto") @RequestParam String nome) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorNome(nome);
        ApiResponseWrapper<List<ProdutoResponseDTO>> response =
                new ApiResponseWrapper<>(true, produtos, "Busca realizada com sucesso");
        return ResponseEntity.ok(response);
    }
}