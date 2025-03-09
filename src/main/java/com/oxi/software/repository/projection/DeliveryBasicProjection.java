package com.oxi.software.repository.projection;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryBasicProjection {
    Long getDeliveryId();
    String getDeliveryState();
    LocalDateTime getCreatedAt();
    Long getOrderId();
    String getOrderState();
    Double getOrderTotal();
    Boolean getPriority();
    String getClientName();
    String getClientAddress();
    String getClientPhone();
}