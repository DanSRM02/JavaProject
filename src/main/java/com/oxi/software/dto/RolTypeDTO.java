package com.oxi.software.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolTypeDTO {

    private Long id;
    private String name;
    private String description;

}
