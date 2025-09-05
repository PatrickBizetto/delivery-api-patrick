package com.delivery_api.service.impl;

import com.delivery_api.dto.CalculoPedidoDTO;
import com.delivery_api.dto.CalculoPedidoResponseDTO;
import com.delivery_api.dto.ItemPedidoDTO;
import com.delivery_api.dto.PedidoDTO;
import com.delivery_api.dto.PedidoResponseDTO;
import com.delivery_api.model.*;
import com.delivery_api.enums.StatusPedido;
import com.delivery_api.exception.BusinessException;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.repository.*;
import com.delivery_api.service.PedidoService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public PedidoResponseDTO criarPedido(PedidoDTO dto) {
        // 1. Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        if (!cliente.isAtivo()) {
            throw new BusinessException("Cliente inativo não pode fazer pedidos");
        }

        // 2. Validar restaurante
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));
        if (!restaurante.isAtivo()) {
            throw new BusinessException("Restaurante não está disponível");
        }

        // 3. Validar produtos e criar itens
        List<ItemPedido> itensPedido = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProdutoId()));
            if (!produto.isDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }
            if (!produto.getRestaurante().getId().equals(dto.getRestauranteId())) {
                throw new BusinessException("Produto não pertence ao restaurante selecionado");
            }

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setSubtotal(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));
            
            itensPedido.add(item);
            subtotal = subtotal.add(item.getSubtotal());
        }

        // 4. Calcular total
        BigDecimal taxaEntrega = restaurante.getTaxaEntrega();
        BigDecimal valorTotal = subtotal.add(taxaEntrega);

        // 5. Criar e salvar pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setEnderecoEntrega(dto.getEnderecoEntrega());
        pedido.setSubtotal(subtotal);
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(valorTotal);
        
        // Associar itens ao pedido antes de salvar
        for (ItemPedido item : itensPedido) {
            item.setPedido(pedido);
        }
        pedido.setItens(itensPedido);
        
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 6. Retornar DTO
        return modelMapper.map(pedidoSalvo, PedidoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));
        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return pedidos.stream()
                .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        if (!isTransicaoValida(pedido.getStatus(), novoStatus)) {
            throw new BusinessException("Transição de status inválida: " + pedido.getStatus() + " -> " + novoStatus);
        }
        pedido.setStatus(novoStatus);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return modelMapper.map(pedidoAtualizado, PedidoResponseDTO.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedidoDTO item : itens) {
            Produto produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
            BigDecimal subtotalItem = produto.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(subtotalItem);
        }
        return total;
    }

    @Override
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        if (!podeSerCancelado(pedido.getStatus())) {
            throw new BusinessException("Pedido não pode ser cancelado no status: " + pedido.getStatus());
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidos(StatusPedido status, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        // Lógica de busca com filtros precisa ser implementada no repositório.
        // Por enquanto, usaremos o findAll como exemplo simples.
        Page<Pedido> pedidosPage = pedidoRepository.findAll(pageable);
        return pedidosPage.map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, StatusPedido status) {
        // A lógica de filtro por status pode ser adicionada aqui ou no repositório.
        List<Pedido> pedidos = pedidoRepository.findByRestauranteId(restauranteId);
        return pedidos.stream()
                .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CalculoPedidoResponseDTO calcularTotalPedido(CalculoPedidoDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemPedidoDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProdutoId()));
            subtotal = subtotal.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));
        }

        BigDecimal taxaEntrega = restaurante.getTaxaEntrega();
        BigDecimal valorTotal = subtotal.add(taxaEntrega);

        CalculoPedidoResponseDTO response = new CalculoPedidoResponseDTO();
        response.setSubtotalItens(subtotal);
        response.setTaxaEntrega(taxaEntrega);
        response.setValorTotal(valorTotal);

        return response;
    }

    private boolean isTransicaoValida(StatusPedido statusAtual, StatusPedido novoStatus) {
        switch (statusAtual) {
            case PENDENTE:
                return novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.CANCELADO;
            case CONFIRMADO:
                return novoStatus == StatusPedido.PREPARANDO || novoStatus == StatusPedido.CANCELADO;
            case PREPARANDO:
                return novoStatus == StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA:
                return novoStatus == StatusPedido.ENTREGUE;
            default:
                return false;
        }
    }

    private boolean podeSerCancelado(StatusPedido status) {
        return status == StatusPedido.PENDENTE || status == StatusPedido.CONFIRMADO;
    }

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            return null;
        }
        return (Usuario) authentication.getPrincipal();
    }

    public boolean canAccess(Long pedidoId) {
        Usuario usuarioLogado = getUsuarioLogado();
        if (usuarioLogado == null) return false;

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        
        boolean isClientOwner = pedido.getCliente().getId().equals(usuarioLogado.getId());
        boolean isRestaurantOwner = pedido.getRestaurante().getId().equals(usuarioLogado.getRestauranteId());

        return isClientOwner || isRestaurantOwner;
    }

    public boolean isClientOwner(Long pedidoId) {
        Usuario usuarioLogado = getUsuarioLogado();
        if (usuarioLogado == null) return false;

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        return pedido.getCliente().getId().equals(usuarioLogado.getId());
    }

    public boolean isRestaurantOwner(Long pedidoId) {
        Usuario usuarioLogado = getUsuarioLogado();
        if (usuarioLogado == null || usuarioLogado.getRestauranteId() == null) return false;

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        return pedido.getRestaurante().getId().equals(usuarioLogado.getRestauranteId());
    }
}