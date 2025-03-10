package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineDTO {
    private Long id;
    private Integer quantity;
    @JsonProperty("product_variant")
    private ProductVariantDTO productVariant;
}


