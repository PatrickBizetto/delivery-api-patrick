package com.delivery_api.repository;

import com.delivery_api.model.Produto;
import com.delivery_api.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.lang.NonNull; // Add this import

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @NonNull
    List<Produto> findByRestauranteAndDisponivelTrue(Restaurante restaurante);
    
    @NonNull
    List<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId);
    
    @NonNull
    List<Produto> findByCategoriaAndDisponivelTrue(String categoria);
    
    @NonNull
    List<Produto> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome);
    
    @NonNull
    List<Produto> findByPrecoBetweenAndDisponivelTrue(BigDecimal precoMin, BigDecimal precoMax);
    
    @NonNull
    List<Produto> findByPrecoLessThanEqualAndDisponivelTrue(BigDecimal preco);
    
    @NonNull
    List<Produto> findByDisponivelTrueOrderByPrecoAsc();
    
    @NonNull
    List<Produto> findByDisponivelTrueOrderByPrecoDesc();

    @Query("SELECT p FROM Produto p JOIN p.itensPedido ip GROUP BY p ORDER BY COUNT(ip) DESC")
    @NonNull
    List<Produto> findProdutosMaisVendidos();

    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.categoria = :categoria AND p.disponivel = true")
    @NonNull
    List<Produto> findByRestauranteAndCategoria(@Param("restauranteId") Long restauranteId, @Param("categoria") String categoria);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.disponivel = true")
    Long countByRestauranteId(@Param("restauranteId") Long restauranteId);

    @Query(value = "SELECT p.nome, COUNT(ip.produto_id) as quantidade_vendida " +
                   "FROM produto p " +
                   "LEFT JOIN item_pedido ip ON p.id = ip.produto_id " +
                   "GROUP BY p.id, p.nome " +
                   "ORDER BY quantidade_vendida DESC " +
                   "LIMIT 5", nativeQuery = true)
    @NonNull
    List<Object[]> produtosMaisVendidos();

    @NonNull
    List<Produto> findByRestauranteId(Long restauranteId);
    
    // Add this method to find all products
    @Override
    @NonNull
    List<Produto> findAll();
}