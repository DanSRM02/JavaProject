package com.oxi.software.dto.google;

import lombok.Data;

import java.util.List;

@Data
public class Route {
    private List<Leg> legs;
    private OverviewPolyline overviewPolyline;

    public int getTotalDuration() {
        return legs.stream()
                .mapToInt(leg -> leg.getDuration().getValue())
                .sum();
    }
}

