package com.oxi.software.dto.google;

import com.oxi.software.utilities.types.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryResponse {
    private Long id;
    private String state;
    private String optimizedRoute;
    private LocalDateTime startTime;
    private String estimatedDuration;
    private String distance;
}
