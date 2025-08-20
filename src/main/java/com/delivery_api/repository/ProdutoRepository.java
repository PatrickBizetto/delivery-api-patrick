package com.delivery_api.repository;

import com.delivery_api.model.Produto;
import com.delivery_api.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByRestauranteAndDisponivelTrue(Restaurante restaurante);
    List<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId);
    List<Produto> findByCategoriaAndDisponivelTrue(String categoria);
    List<Produto> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome);
    List<Produto> findByPrecoBetweenAndDisponivelTrue(BigDecimal precoMin, BigDecimal precoMax);
    List<Produto> findByPrecoLessThanEqualAndDisponivelTrue(BigDecimal preco);
    List<Produto> findByDisponivelTrueOrderByPrecoAsc();
    List<Produto> findByDisponivelTrueOrderByPrecoDesc();

    @Query("SELECT p FROM Produto p JOIN p.itensPedido ip GROUP BY p ORDER BY COUNT(ip) DESC")
    List<Produto> findProdutosMaisVendidos();

    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.categoria = :categoria AND p.disponivel = true")
    List<Produto> findByRestauranteAndCategoria(@Param("restauranteId") Long restauranteId, @Param("categoria") String categoria);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.disponivel = true")
    Long countByRestauranteId(@Param("restauranteId") Long restauranteId);

    @Query(value = "SELECT p.nome, COUNT(ip.produto_id) as quantidade_vendida " +
                   "FROM produto p " +
                   "LEFT JOIN item_pedido ip ON p.id = ip.produto_id " +
                   "GROUP BY p.id, p.nome " +
                   "ORDER BY quantidade_vendida DESC " +
                   "LIMIT 5", nativeQuery = true)
    List<Object[]> produtosMaisVendidos();
}
