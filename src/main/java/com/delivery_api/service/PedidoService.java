package com.delivery_api.service;

import com.delivery_api.dto.PedidoDTO;
import com.delivery_api.dto.PedidoResponseDTO;
import com.delivery_api.dto.ItemPedidoDTO;
import com.delivery_api.enums.StatusPedido;
import java.math.BigDecimal;
import java.util.List;

public interface PedidoService {

    PedidoResponseDTO criarPedido(PedidoDTO dto);

    PedidoResponseDTO buscarPedidoPorId(Long id);

    List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId);

    PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido status);

    BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens);

    void cancelarPedido(Long id);
}