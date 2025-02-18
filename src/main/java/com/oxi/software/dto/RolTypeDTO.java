package com.oxi.software.dto;

import com.oxi.software.entities.RolType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolTypeDTO {

    private Long id;
    private String name;
    private String description;

    public RolTypeDTO(RolType rolType) {
    }
}
