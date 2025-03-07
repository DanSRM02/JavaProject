package com.oxi.software.repository;

import com.oxi.software.entity.Delivery;
import com.oxi.software.repository.projection.DeliveryProjection;
import com.oxi.software.repository.projection.OrderSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findDeliveryById(Long domiciliaryId);
}
