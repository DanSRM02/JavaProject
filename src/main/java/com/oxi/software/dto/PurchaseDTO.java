package com.oxi.software.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseDTO {

    private Long id;
    private Integer total;
    private DeliveryDTO delivery;

}
