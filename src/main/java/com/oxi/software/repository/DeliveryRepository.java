package com.oxi.software.repository;

import com.oxi.software.entity.Delivery;
import com.oxi.software.repository.projection.DeliveryBasicProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("SELECT " +
            "d.id AS deliveryId, " +
            "d.state AS deliveryState, " +
            "d.createdAt AS createdAt, " +
            "o.id AS orderId, " +
            "o.state AS orderState, " +
            "o.total AS orderTotal, " +
            "o.priority AS priority, " +
            "i.name AS clientName, " +
            "i.address AS clientAddress, " +
            "i.phone AS clientPhone " +
            "FROM Delivery d " +
            "JOIN d.order o " +
            "JOIN o.user u " +
            "JOIN u.individual i " +
            "WHERE d.domiciliary.id = :domiciliaryId") // <- Cambio clave aquí
    List<DeliveryBasicProjection> findDeliveriesByDomiciliary(@Param("domiciliaryId") Long domiciliaryId);

}
