package com.delivery_api.repository;

import com.delivery_api.model.Restaurante;
import com.delivery_api.projection.RelatorioVendas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    Optional<Restaurante> findByNome(String nome);
    List<Restaurante> findByCategoria(String categoria);
    List<Restaurante> findByAtivoTrue();
    List<Restaurante> findByCategoriaAndAtivoTrue(String categoria);
    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxaEntrega);
    List<Restaurante> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    List<Restaurante> findTop5ByOrderByNomeAsc();

    @Query("SELECT DISTINCT r FROM Restaurante r JOIN r.produtos p WHERE r.ativo = true")
    List<Restaurante> findRestaurantesComProdutos();

    @Query("SELECT r FROM Restaurante r WHERE r.taxaEntrega BETWEEN :min AND :max AND r.ativo = true")
    List<Restaurante> findByTaxaEntregaBetween(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    @Query("SELECT DISTINCT r.categoria FROM Restaurante r WHERE r.ativo = true ORDER BY r.categoria")
    List<String> findCategoriasDisponiveis();

    boolean existsByTelefone(String telefone);

    @Query("SELECT r.nome as nomeRestaurante, " +
           "SUM(p.valorTotal) as totalVendas, " +
           "COUNT(p.id) as quantidadePedidos " +
           "FROM Restaurante r " +
           "LEFT JOIN Pedido p ON r.id = p.restaurante.id " +
           "GROUP BY r.id, r.nome")
    List<RelatorioVendas> relatorioVendasPorRestaurante();
}
