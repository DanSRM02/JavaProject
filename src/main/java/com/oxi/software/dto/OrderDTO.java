package com.oxi.software.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

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
    private List<OrderLineDTO> orderLines;
    private Date createdAt;

}

