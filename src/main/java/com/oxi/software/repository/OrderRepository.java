package com.oxi.software.repository;

import com.oxi.software.entity.Order;
import com.oxi.software.repository.projection.OrderDetailsProjection;
import com.oxi.software.repository.projection.OrderSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<OrderSummaryProjection> findByState(String state);

    @Query("""
        SELECT 
           p.name AS productName,       
           pv.price AS variantPrice,       
           ol.quantity AS quantityOrdered,
           u.acronym AS unitAcronym,
           u.unitType AS unitType
        FROM OrderLine ol
        JOIN ol.productVariant pv
        JOIN pv.product p
        JOIN pv.unit u
        WHERE ol.order.id = :orderId
    """)
    List<OrderDetailsProjection> findOrderDetailsById(@Param("orderId") Long orderId);

}
