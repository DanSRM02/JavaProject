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

    @Query("""
  SELECT\s
      o.id AS id,
      o.state AS orderState,
      i.email AS userIndividualEmail,
      i.phone AS userIndividualPhone,
      i.address AS userIndividualAddress,
      o.total AS total,
      o.createdAt AS createdAt,
      i.name AS userIndividualName,
      d.deliveryState AS deliveryState,
      di.name AS deliveryPersonName
  FROM Order o
  JOIN o.user u
  JOIN u.individual i
  LEFT JOIN o.delivery d
  LEFT JOIN d.user du
  LEFT JOIN du.individual di
  WHERE o.state =:state
  ORDER BY o.id ASC   \s
""")
    List<OrderSummaryProjection> findByState(@Param("state") String state);


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
