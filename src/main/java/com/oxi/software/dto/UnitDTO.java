package com.oxi.software.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitDTO {

    private Long id;
    private String unitType;
    private String acronym;

}
