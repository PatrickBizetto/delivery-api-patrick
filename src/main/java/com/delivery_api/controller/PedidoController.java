package com.delivery_api.controller;

import com.delivery_api.dto.*;
import com.delivery_api.enums.StatusPedido;
import com.delivery_api.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- ADICIONE ESTE IMPORT
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Operações relacionadas aos pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // REGRA: Apenas usuários com perfil CLIENTE podem criar um novo pedido.
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do pedido a ser criado")
    @Operation(summary = "Criar pedido (Cliente)",
            description = "Cria um novo pedido no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> criarPedido(
            @Valid @RequestBody
            PedidoDTO dto) {
        PedidoResponseDTO pedido = pedidoService.criarPedido(dto);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Pedido criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // REGRA: Apenas ADMIN ou o CLIENTE/RESTAURANTE donos do pedido podem vê-lo.
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @pedidoServiceImpl.canAccess(#id)")
    @Operation(summary = "Buscar pedido por ID (Admin ou Dono do Pedido)",
            description = "Recupera um pedido específico com todos os detalhes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> buscarPorId(
            @Parameter(description = "ID do pedido")
            @PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Pedido encontrado");
        return ResponseEntity.ok(response);
    }

    // REGRA: Apenas ADMIN pode listar TODOS os pedidos do sistema.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os pedidos (Admin)",
            description = "Lista pedidos com filtros opcionais e paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> listar(
            @Parameter(description = "Status do pedido")
            @RequestParam(required = false) StatusPedido status,
            @Parameter(description = "Data inicial")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @Parameter(description = "Parâmetros de paginação")
            Pageable pageable) {
        Page<PedidoResponseDTO> pedidos =
                pedidoService.listarPedidos(status, dataInicio, dataFim, pageable);
        PagedResponseWrapper<PedidoResponseDTO> response =
                new PagedResponseWrapper<>(pedidos);
        return ResponseEntity.ok(response);
    }

    // REGRA: Apenas ADMIN ou o RESTAURANTE dono do pedido pode atualizar o status.
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @pedidoServiceImpl.isRestaurantOwner(#id))")
    @Operation(summary = "Atualizar status do pedido (Admin ou Restaurante)",
            description = "Atualiza o status de um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> atualizarStatus(
            @Parameter(description = "ID do pedido")
            @PathVariable Long id,
            @Valid @RequestBody StatusPedidoDTO statusDTO) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id,
                statusDTO.getStatus());
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Status atualizado com sucesso");
        return ResponseEntity.ok(response);
    }

    // REGRA: Apenas ADMIN ou o CLIENTE dono do pedido pode cancelar.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @pedidoServiceImpl.isClientOwner(#id)")
    @Operation(summary = "Cancelar pedido (Admin ou Cliente)",
            description = "Cancela um pedido se possível")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> cancelarPedido(
            @Parameter(description = "ID do pedido")
            @PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    // REGRA: Apenas ADMIN ou o próprio CLIENTE pode ver seu histórico.
    // A expressão SpEL '#clienteId == principal.id' compara o ID da URL com o ID do usuário logado.
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN') or #clienteId == principal.id")
    @Operation(summary = "Histórico do cliente (Admin ou Próprio Cliente)",
            description = "Lista todos os pedidos de um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico recuperado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApiResponseWrapper<List<PedidoResponseDTO>>> buscarPorCliente(
            @Parameter(description = "ID do cliente")
            @PathVariable Long clienteId) {
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        ApiResponseWrapper<List<PedidoResponseDTO>> response =
                new ApiResponseWrapper<>(true, pedidos, "Histórico recuperado com sucesso");
        return ResponseEntity.ok(response);
    }

    // REGRA: Apenas ADMIN ou o próprio RESTAURANTE pode ver seus pedidos.
    // A expressão SpEL '#restauranteId == principal.restauranteId' compara o ID da URL com o ID do restaurante do usuário logado.
    @GetMapping("/restaurante/{restauranteId}")
    @PreAuthorize("hasRole('ADMIN') or #restauranteId == principal.restauranteId")
    @Operation(summary = "Pedidos do restaurante (Admin ou Próprio Restaurante)",
            description = "Lista todos os pedidos de um restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos recuperados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApiResponseWrapper<List<PedidoResponseDTO>>> buscarPorRestaurante(
            @Parameter(description = "ID do restaurante")
            @PathVariable Long restauranteId,
            @Parameter(description = "Status do pedido")
            @RequestParam(required = false) StatusPedido status) {
        List<PedidoResponseDTO> pedidos =
                pedidoService.buscarPedidosPorRestaurante(restauranteId, status);
        ApiResponseWrapper<List<PedidoResponseDTO>> response =
                new ApiResponseWrapper<>(true, pedidos, "Pedidos recuperados com sucesso");
        return ResponseEntity.ok(response);
    }

    // REGRA: Endpoint utilitário para clientes logados calcularem o valor de um carrinho.
    @PostMapping("/calcular")
    @PreAuthorize("isAuthenticated()")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Itens para cálculo")
    @Operation(summary = "Calcular total do pedido (Autenticado)",
            description = "Calcula o total de um pedido sem salvá-lo")
    public ResponseEntity<ApiResponseWrapper<CalculoPedidoResponseDTO>> calcularTotal(
            @Valid @RequestBody
            CalculoPedidoDTO dto) {
        CalculoPedidoResponseDTO calculo = pedidoService.calcularTotalPedido(dto);
        ApiResponseWrapper<CalculoPedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, calculo, "Total calculado com sucesso");
        return ResponseEntity.ok(response);
    }
}