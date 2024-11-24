package com.oxi.software.dto;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitDTO {

    private Long id;
    private String unitType;
    private Date createdAt;
    private Date updatedAt;

}
