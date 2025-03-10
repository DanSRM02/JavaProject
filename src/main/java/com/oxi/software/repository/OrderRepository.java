package com.oxi.software.repository;

import com.oxi.software.entity.Order;
import com.oxi.software.repository.projection.KanbanOrderProjection;
import com.oxi.software.repository.projection.OrderDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
    SELECT
        o.id AS id,
        o.state AS orderState,
        o.total AS total,
        i.name AS userIndividualName,
        i.email AS email,
        i.address AS address,
        o.createdAt AS createdAt,
        di.name AS deliveryPersonName
    FROM Order o
    JOIN o.user u
    JOIN u.individual i
    LEFT JOIN o.deliveries d
    LEFT JOIN d.domiciliary du
    LEFT JOIN du.individual di
    ORDER BY o.createdAt ASC
""")
    List<KanbanOrderProjection> findKanbanOrders();

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

    List<Order> findByUserId(Long userId);

}
