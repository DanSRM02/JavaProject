package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {

    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("variant_price")
    private Integer variantPrice;
    private UnitDTO unit;
    @JsonProperty("quantity_ordered")
    private Integer quantityOrdered;
}
