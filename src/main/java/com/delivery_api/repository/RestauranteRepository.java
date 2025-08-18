package com.delivery_api.repository;

import com.delivery_api.model.Restaurante;
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
    List<Restaurante> findByAtivoTrue();
    List<Restaurante> findByCategoriaAndAtivoTrue(String categoria);
    List<Restaurante> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    
    // Supondo que você adicione um campo 'avaliacao' na entidade Restaurante
    // List<Restaurante> findByAvaliacaoGreaterThanEqualAndAtivoTrue(BigDecimal avaliacao);
    // List<Restaurante> findByAtivoTrueOrderByAvaliacaoDesc();

    @Query("SELECT DISTINCT r FROM Restaurante r JOIN r.produtos p WHERE r.ativo = true")
    List<Restaurante> findRestaurantesComProdutos();

    @Query("SELECT r FROM Restaurante r WHERE r.taxaEntrega BETWEEN :min AND :max AND r.ativo = true")
    List<Restaurante> findByTaxaEntregaBetween(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    @Query("SELECT DISTINCT r.categoria FROM Restaurante r WHERE r.ativo = true ORDER BY r.categoria")
    List<String> findCategoriasDisponiveis();
}
