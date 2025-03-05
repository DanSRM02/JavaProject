package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryProDTO {
    Long id;
    String name;
    String phone;
    @JsonProperty("rol_type")
    String roleName;
}
