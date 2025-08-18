package com.delivery_api.controller;

import com.delivery_api.model.Pedido;
import com.delivery_api.enums.StatusPedido;
import com.delivery_api.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestParam Long clienteId, @RequestParam Long restauranteId) {
        try {
            Pedido pedido = pedidoService.criarPedido(clienteId, restauranteId);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/{pedidoId}/itens")
    public ResponseEntity<?> adicionarItem(@PathVariable Long pedidoId, @RequestParam Long produtoId, @RequestParam Integer quantidade) {
        try {
            Pedido pedido = pedidoService.adicionarItem(pedidoId, produtoId, quantidade);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/{pedidoId}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long pedidoId) {
        try {
            Pedido pedido = pedidoService.confirmarPedido(pedidoId);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/{pedidoId}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long pedidoId, @RequestParam StatusPedido status) {
        try {
            Pedido pedido = pedidoService.atualizarStatus(pedidoId, status);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }
    
    @PutMapping("/{pedidoId}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long pedidoId, @RequestParam(required = false) String motivo) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(pedidoId, motivo);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<Pedido> buscarPorNumero(@PathVariable String numeroPedido) {
        return pedidoService.buscarPorNumero(numeroPedido)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
