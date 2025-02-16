package com.oxi.software.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDTO {

    private Long id;
    private String deliveryState;
    private OrderDTO order;
    private UserDTO domiciliary ;

}

