package com.oxi.software.repository.projection;

import java.time.LocalDateTime;

public interface KanbanOrderProjection {
    Long getId();
    String getOrderState();
    Double getTotal();
    String getUserIndividualName();
    String getEmail();
    String getAddress();
    String getCreatedAt();
    String getDeliveryPersonName();
}

