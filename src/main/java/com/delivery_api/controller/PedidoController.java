package com.delivery_api.controller;

import com.delivery_api.dto.*;
import com.delivery_api.enums.StatusPedido;
import com.delivery_api.service.PedidoService;
// 🔹 IMPORTS DO SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Operações relacionadas aos pedidos. Requer autenticação.")
@SecurityRequirement(name = "bearerAuth") // 🔹 APLICA SEGURANÇA A TODOS OS ENDPOINTS DA CLASSE
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Criar um novo pedido (Cliente)",
               description = "Cria um novo pedido no sistema associado ao cliente autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: itens do pedido em branco)"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado (usuário não é um CLIENTE)")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> criarPedido(@Valid @RequestBody PedidoDTO dto) {
        PedidoResponseDTO pedido = pedidoService.criarPedido(dto);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Pedido criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @pedidoServiceImpl.canAccess(#id)")
    @Operation(summary = "Buscar pedido por ID (Admin ou Dono do Pedido)",
               description = "Recupera um pedido específico com todos os detalhes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> buscarPorId(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Pedido encontrado");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os pedidos (Admin)",
               description = "Lista todos os pedidos do sistema com filtros opcionais e paginação. O Springdoc adiciona automaticamente os parâmetros para paginação (page, size, sort).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos recuperada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> listar(
            @Parameter(description = "Filtrar por status do pedido") @RequestParam(required = false) StatusPedido status,
            @Parameter(description = "Filtrar por data inicial (formato YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Filtrar por data final (formato YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Pageable pageable) {
        Page<PedidoResponseDTO> pedidos =
                pedidoService.listarPedidos(status, dataInicio, dataFim, pageable);
        PagedResponseWrapper<PedidoResponseDTO> response =
                new PagedResponseWrapper<>(pedidos);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @pedidoServiceImpl.isRestaurantOwner(#id))")
    @Operation(summary = "Atualizar status do pedido (Admin ou Restaurante)",
               description = "Atualiza o status de um pedido (ex: de PENDENTE para PREPARANDO).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Status inválido fornecido"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> atualizarStatus(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Valid @RequestBody StatusPedidoDTO statusDTO) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id,
                statusDTO.getStatus());
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Status atualizado com sucesso");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @pedidoServiceImpl.isClientOwner(#id)")
    @Operation(summary = "Cancelar pedido (Admin ou Cliente)",
               description = "Cancela um pedido, se o status atual permitir o cancelamento.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado (não é o dono ou status não permite)"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<Void> cancelarPedido(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN') or #clienteId == principal.id")
    @Operation(summary = "Listar pedidos de um cliente (Admin ou Próprio Cliente)",
               description = "Retorna o histórico de todos os pedidos de um cliente específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Histórico recuperado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<List<PedidoResponseDTO>>> buscarPorCliente(@Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        ApiResponseWrapper<List<PedidoResponseDTO>> response =
                new ApiResponseWrapper<>(true, pedidos, "Histórico recuperado com sucesso");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurante/{restauranteId}")
    @PreAuthorize("hasRole('ADMIN') or #restauranteId == principal.restauranteId")
    @Operation(summary = "Listar pedidos de um restaurante (Admin ou Próprio Restaurante)",
               description = "Retorna todos os pedidos recebidos por um restaurante específico, com filtro opcional por status.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos recuperados com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<List<PedidoResponseDTO>>> buscarPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId,
            @Parameter(description = "Status do pedido para filtrar") @RequestParam(required = false) StatusPedido status) {
        List<PedidoResponseDTO> pedidos =
                pedidoService.buscarPedidosPorRestaurante(restauranteId, status);
        ApiResponseWrapper<List<PedidoResponseDTO>> response =
                new ApiResponseWrapper<>(true, pedidos, "Pedidos recuperados com sucesso");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/calcular")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Calcular total do pedido (Autenticado)",
               description = "Calcula o valor total de um pedido com base nos itens fornecidos, sem criar o pedido no banco de dados. Útil para simulação de carrinho.")
    @ApiResponses({ // 🔹 ADICIONADO BLOCO DE RESPOSTAS
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<ApiResponseWrapper<CalculoPedidoResponseDTO>> calcularTotal(@Valid @RequestBody CalculoPedidoDTO dto) {
        CalculoPedidoResponseDTO calculo = pedidoService.calcularTotalPedido(dto);
        ApiResponseWrapper<CalculoPedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, calculo, "Total calculado com sucesso");
        return ResponseEntity.ok(response);
    }
}