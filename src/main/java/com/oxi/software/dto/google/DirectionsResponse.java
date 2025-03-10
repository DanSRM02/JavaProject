package com.oxi.software.dto.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionsResponse {
    private List<Route> routes;
    private String status;
}