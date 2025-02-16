package com.oxi.software.repository;

import com.oxi.software.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    /**
     * Retorna true si existe un ProductVariant que coincida
     * con el ID de producto y el ID de unidad dados.
     */
    boolean existsByProductIdAndUnitId(Long productId, Long unitId);
}
