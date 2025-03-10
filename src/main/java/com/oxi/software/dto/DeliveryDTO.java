package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDTO {

    private Long id;
    @JsonProperty("delivery_state")
    private String deliveryState;
    private OrderDTO order;
    @JsonProperty("start_time")
    private LocalDateTime startTime;
    private UserDTO domiciliary;

}

