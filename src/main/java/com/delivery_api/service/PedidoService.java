package com.delivery_api.service;

import com.delivery_api.model.*;
import com.delivery_api.enums.StatusPedido;
import com.delivery_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    public Pedido criarPedido(Long clienteId, Long restauranteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));

        if (!cliente.isAtivo()) {
            throw new IllegalArgumentException("Cliente inativo não pode fazer pedidos");
        }
        if (!restaurante.isAtivo()) {
            throw new IllegalArgumentException("Restaurante não está disponível");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.PENDENTE);
        return pedidoRepository.save(pedido);
    }

    public Pedido adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));

        if (!produto.isDisponivel()) {
            throw new IllegalArgumentException("Produto não disponível: " + produto.getNome());
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (!produto.getRestaurante().getId().equals(pedido.getRestaurante().getId())) {
            throw new IllegalArgumentException("Produto não pertence ao restaurante do pedido");
        }

        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        item.calcularSubtotal();

        pedido.adicionarItem(item);
        return pedidoRepository.save(pedido);
    }

    public Pedido confirmarPedido(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Apenas pedidos pendentes podem ser confirmados");
        }
        if (pedido.getItens().isEmpty()) {
            throw new IllegalStateException("Pedido deve ter pelo menos um item");
        }

        pedido.confirmar();
        return pedidoRepository.save(pedido);
    }

    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));
        // Adicione aqui a lógica de transição de status se necessário
        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    public Pedido cancelarPedido(Long pedidoId, String motivo) {
        Pedido pedido = buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        if (pedido.getStatus() == StatusPedido.ENTREGUE || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalStateException("Pedido já finalizado ou cancelado não pode ser alterado.");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        if (motivo != null && !motivo.trim().isEmpty()) {
            pedido.setObservacoes((pedido.getObservacoes() == null ? "" : pedido.getObservacoes()) + " | Cancelado: " + motivo);
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorNumero(String numeroPedido) {
        return Optional.ofNullable(pedidoRepository.findByNumeroPedido(numeroPedido));
    }
}
