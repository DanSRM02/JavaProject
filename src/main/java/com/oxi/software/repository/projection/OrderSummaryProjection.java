package com.oxi.software.repository.projection;

import java.util.Date;

public interface OrderSummaryProjection {
    Long getId();
    String getOrderState();
    String getUserIndividualEmail();
    String getUserIndividualAddress();
    Double getTotal();
    Date getCreatedAt();
    String getUserIndividualName();
    // Nuevo campo agregado
    String getDeliveryPersonName();
}
