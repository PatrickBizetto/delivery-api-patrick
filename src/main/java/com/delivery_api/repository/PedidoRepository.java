package com.delivery_api.repository;

import com.delivery_api.model.Pedido;
import com.delivery_api.model.Cliente;
import com.delivery_api.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteOrderByDataPedidoDesc(Cliente cliente);
    
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.cliente.id = :clienteId ORDER BY p.dataPedido DESC")
    List<Pedido> findByClienteIdOrderByDataPedidoDesc(Long clienteId);
    
    List<Pedido> findByStatusOrderByDataPedidoDesc(StatusPedido status);
    Pedido findByNumeroPedido(String numeroPedido);
    List<Pedido> findByDataPedidoBetweenOrderByDataPedidoDesc(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT p FROM Pedido p WHERE CAST(p.dataPedido AS localdate) = CURRENT_DATE ORDER BY p.dataPedido DESC")
    List<Pedido> findPedidosDoDia();

    @Query("SELECT p FROM Pedido p WHERE p.restaurante.id = :restauranteId ORDER BY p.dataPedido DESC")
    List<Pedido> findByRestauranteId(@Param("restauranteId") Long restauranteId);

    @Query("SELECT p.status, COUNT(p) FROM Pedido p GROUP BY p.status")
    List<Object[]> countPedidosByStatus();

    @Query("SELECT p FROM Pedido p WHERE p.status IN ('PENDENTE', 'CONFIRMADO', 'PREPARANDO') ORDER BY p.dataPedido ASC")
    List<Pedido> findPedidosPendentes();

    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim AND p.status NOT IN ('CANCELADO')")
    BigDecimal calcularVendasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT p.restaurante.nome, SUM(p.valorTotal) " +
           "FROM Pedido p " +
           "GROUP BY p.restaurante.id, p.restaurante.nome " +
           "ORDER BY SUM(p.valorTotal) DESC")
    List<Object[]> calcularTotalVendasPorRestaurante();

    @Query("SELECT p FROM Pedido p WHERE p.valorTotal > :valor ORDER BY p.valorTotal DESC")
    List<Pedido> buscarPedidosComValorAcimaDe(@Param("valor") BigDecimal valor);

    @Query("SELECT p FROM Pedido p " +
           "WHERE p.dataPedido BETWEEN :inicio AND :fim " +
           "AND p.status = :status " +
           "ORDER BY p.dataPedido DESC")
    List<Pedido> relatorioPedidosPorPeriodoEStatus(
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        @Param("status") StatusPedido status
    );
}
