package com.oxi.software.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private Long id;
    private Boolean state;
    private UserDTO user;
    private List<ProductDTO> productList;
    private Date createdAt;

}

