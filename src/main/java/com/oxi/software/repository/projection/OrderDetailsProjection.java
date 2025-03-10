package com.oxi.software.repository.projection;

import java.util.List;

public interface OrderDetailsProjection {
    //Nombre del producto
    String getProductName();

    // Precio de la variante
    Integer getVariantPrice();

    // Cantidad pedida en la order_line
    Integer getQuantityOrdered();

    // Atributos de la unidad
    String getUnitAcronym();
    String getUnitType();
}

