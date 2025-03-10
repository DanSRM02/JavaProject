package com.oxi.software.repository;

import com.oxi.software.entity.Product;
import com.oxi.software.repository.projection.ProductDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT " +
            "ol.order.id AS orderId, " +
            "p.name AS productName, " +
            "ol.quantity AS quantityOrdered, " +
            "u.acronym AS unitAcronym, " +
            "u.unitType AS unitType, " +
            "pv.price AS unitPrice " +
            "FROM OrderLine ol " +
            "JOIN ol.productVariant pv " +
            "JOIN pv.product p " +
            "JOIN pv.unit u " +
            "WHERE ol.order.id IN :orderIds")
    List<ProductDetailProjection> findProductsByOrderIds(@Param("orderIds") Set<Long> orderIds);
}
