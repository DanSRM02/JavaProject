package com.oxi.software.dto.google;

import lombok.Data;

import java.util.List;

@Data
public class GeocodingResponse {
    private List<GeocodingResult> results;
    private String status;
}