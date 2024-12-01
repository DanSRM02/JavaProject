package com.oxi.software.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfOrderDTO {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private List<ProductDTO> productList;
    private int totalAmount;
    private String orderDate;
}
