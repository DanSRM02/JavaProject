package com.oxi.software.dto.google;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteCalculationResult {
    private String polyline;
    private String formattedDuration;
    private int durationInSeconds;
}