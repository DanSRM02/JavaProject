package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class OrderDTO {

    private Long id;
    private String state;
    private Boolean priority;
    private Double total;
    private UserDTO user;
    private List<OrderLineDTO> orderLines = new ArrayList<>();
    @JsonProperty("large_delivery")
    private Boolean largeDelivery;
    private Date createdAt;

}

