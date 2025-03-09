package com.oxi.software.dto.google;

import lombok.Data;

@Data
public class Duration {
    private String text; // Ej: "35 mins"
    private int value; // Duración en segundos
}
