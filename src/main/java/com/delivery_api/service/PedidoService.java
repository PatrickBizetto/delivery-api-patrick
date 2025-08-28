package com.delivery_api.service;

import com.delivery_api.dto.PedidoDTO;
import com.delivery_api.dto.PedidoResponseDTO;
import com.delivery_api.dto.CalculoPedidoDTO;
import com.delivery_api.dto.CalculoPedidoResponseDTO;
import com.delivery_api.dto.ItemPedidoDTO;
import com.delivery_api.enums.StatusPedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PedidoService {

    PedidoResponseDTO criarPedido(PedidoDTO dto);

    PedidoResponseDTO buscarPedidoPorId(Long id);

    List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId);

    PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido status);

    BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens);

    void cancelarPedido(Long id);

    Page<PedidoResponseDTO> listarPedidos(StatusPedido status, LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    List<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, StatusPedido status);
    
    CalculoPedidoResponseDTO calcularTotalPedido(CalculoPedidoDTO dto);
}