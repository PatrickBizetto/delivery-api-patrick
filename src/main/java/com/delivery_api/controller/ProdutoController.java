package com.delivery_api.controller;

import com.delivery_api.dto.*;
import com.delivery_api.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- ADICIONE ESTE IMPORT
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "Operações relacionadas aos produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // REGRA: Apenas usuários com perfil RESTAURANTE ou ADMIN podem cadastrar produtos.
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do produto a ser criado")
    @Operation(summary = "Cadastrar produto (Restaurante ou Admin)",
            description = "Cria um novo produto no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> cadastrar(
            @Valid @RequestBody
            ProdutoDTO dto) {
        ProdutoResponseDTO produto = produtoService.cadastrarProduto(dto);
        ApiResponseWrapper<ProdutoResponseDTO> response =
                new ApiResponseWrapper<>(true, produto, "Produto criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // REGRA: Endpoint público. Acesso liberado para todos no SecurityConfig.
    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID (Público)",
            description = "Recupera um produto específico pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> buscarPorId(
            @Parameter(description = "ID do produto")
            @PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        ApiResponseWrapper<ProdutoResponseDTO> response =
                new ApiResponseWrapper<>(true, produto, "Produto encontrado");
        return ResponseEntity.ok(response);
    }

    // REGRA: Apenas ADMIN ou o DONO do produto (através do restaurante) pode atualizar.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoServiceImpl.isOwner(#id)")
    @Operation(summary = "Atualizar produto (Admin ou Dono)",
            description = "Atualiza os dados de um produto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> atualizar(
            @Parameter(description = "ID do produto")
            @PathVariable Long id,
            @Valid @RequestBody ProdutoDTO dto) {
        ProdutoResponseDTO produto = produtoService.atualizarProduto(id, dto);
        ApiResponseWrapper<ProdutoResponseDTO> response =
                new ApiResponseWrapper<>(true, produto, "Produto atualizado com sucesso");
        return ResponseEntity.ok(response);
    }

    // REGRA: Apenas ADMIN ou o DONO do produto pode remover.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoServiceImpl.isOwner(#id)")
    @Operation(summary = "Remover produto (Admin ou Dono)",
            description = "Remove um produto do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do produto")
            @PathVariable Long id) {
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }

    // REGRA: Apenas ADMIN ou o DONO do produto pode alterar a disponibilidade.
    @PatchMapping("/{id}/disponibilidade")
    @PreAuthorize("hasRole('ADMIN') or @produtoServiceImpl.isOwner(#id)")
    @Operation(summary = "Alterar disponibilidade (Admin ou Dono)",
            description = "Alterna a disponibilidade do produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidade alterada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> alterarDisponibilidade(
            @Parameter(description = "ID do produto")
            @PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.alterarDisponibilidade(id);
        ApiResponseWrapper<ProdutoResponseDTO> response =
                new ApiResponseWrapper<>(true, produto, "Disponibilidade alterada com sucesso");
        return ResponseEntity.ok(response);
    }

    // REGRA: Endpoint público para listar todos os produtos
    @GetMapping
    @Operation(summary = "Listar todos os produtos (Público)",
        description = "Retorna uma lista de todos os produtos disponíveis")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodosProdutos();
        ApiResponseWrapper<List<ProdutoResponseDTO>> response =
                new ApiResponseWrapper<>(true, produtos, "Lista de produtos");
        return ResponseEntity.ok(response);
    }

    // REGRA: Endpoint público.
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar por categoria (Público)",
            description = "Lista produtos de uma categoria específica")
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Categoria do produto")
            @PathVariable String categoria) {
        List<ProdutoResponseDTO> produtos =
                produtoService.buscarProdutosPorCategoria(categoria);
        ApiResponseWrapper<List<ProdutoResponseDTO>> response =
                new ApiResponseWrapper<>(true, produtos, "Produtos encontrados");
        return ResponseEntity.ok(response);
    }

    // REGRA: Endpoint público.
    @GetMapping("/buscar")
    @Operation(summary = "Buscar por nome (Público)",
            description = "Busca produtos pelo nome")
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> buscarPorNome(
            @Parameter(description = "Nome do produto")
            @RequestParam String nome) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorNome(nome);
        ApiResponseWrapper<List<ProdutoResponseDTO>> response =
                new ApiResponseWrapper<>(true, produtos, "Busca realizada com sucesso");
        return ResponseEntity.ok(response);
    }
}