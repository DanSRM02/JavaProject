package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "product")
public class ProductVariantDTO {
    private Long id;
    private Integer quantity;
    private Integer price;
    private UnitDTO unit;
    @JsonIgnore
    private ProductDTO product;
}
