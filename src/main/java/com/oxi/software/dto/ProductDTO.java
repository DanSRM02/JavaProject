package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "variants")
public class ProductDTO {
    private Long id;
    private String name;
    private Boolean state;
    private List<ProductVariantDTO> variants = new ArrayList<>();
}

