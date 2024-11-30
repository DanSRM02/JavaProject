package com.oxi.software.dto;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private Integer quantity;
    private Boolean state;
    private Integer price;

    private UnitDTO unit;

}
