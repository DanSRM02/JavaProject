package com.oxi.software.dto;

import lombok.*;

import java.util.Date;

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

    private Date createdAt;
    private Date updatedAt;

    private UnitDTO unit;

}
