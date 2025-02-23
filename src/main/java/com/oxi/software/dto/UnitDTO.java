package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitDTO {

    private Long id;
    @JsonProperty("unit_type")
    private String unitType;
    private String acronym;

}
