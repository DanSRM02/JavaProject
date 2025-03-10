package com.oxi.software.dto.google;

import lombok.Data;

@Data
public class Distance {
    private String text; // Ej: "12.4 km"
    private int value; // Distancia en metros
}