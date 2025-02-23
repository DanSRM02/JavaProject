package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDTO {

    private Long id;
    @JsonProperty("delivery_state")
    private String deliveryState;
    private OrderDTO order;
    private UserDTO domiciliary;

}

