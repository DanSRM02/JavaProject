package com.oxi.software.repository.projection;

public interface ProductDetailProjection {
    Long getOrderId();
    String getProductName();
    Integer getQuantityOrdered();
    String getUnitAcronym();
    String getUnitType();
    Double getUnitPrice();
}