package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "product")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVariantDTO {
    private Long id;
    private Integer quantity;
    private Integer price;
    private UnitDTO unit;
    @JsonIgnore
    private ProductDTO product;
}
