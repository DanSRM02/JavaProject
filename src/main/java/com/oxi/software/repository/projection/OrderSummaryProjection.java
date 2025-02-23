package com.oxi.software.repository.projection;

import java.util.Date;

public interface OrderSummaryProjection {
    Long getId();
    String getState();
    String getUserIndividualEmail();
    String getUserIndividualAddress();
    Double getTotal();
    Date getCreatedAt();
    String getUserIndividualName();
}
