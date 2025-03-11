package com.oxi.software.service;

import com.oxi.software.dto.google.GeocodingResponse;
import com.oxi.software.utilities.exception.GeocodingException;
import com.oxi.software.dto.google.GeoLocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GeocodingService {
    private final RestTemplate restTemplate;

    @Value("${google.maps.api-key}")
    private String apiKey;

    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GeoLocation convertAddressToCoordinates(String address) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json"
                + "?address={address}"
                + "&region=co"  // Fuerza búsqueda en Colombia
                + "&key={key}";

        Map<String, String> params = Map.of(
                "address", URLEncoder.encode(address, StandardCharsets.UTF_8),
                "key", apiKey
        );

        GeocodingResponse response = restTemplate.getForObject(url, GeocodingResponse.class, params);

        if (response == null || !"OK".equals(response.getStatus())) {
            throw new GeocodingException("Error en geo codificación", address);
        }

        return response.getResults().stream()
                .filter(result -> result.getGeometry() != null)
                .filter(result -> result.getGeometry().getLocation() != null)
                .filter(result -> isValidCoordinate(result.getGeometry().getLocation()))
                .findFirst()
                .map(result -> {
                    GeoLocation loc = result.getGeometry().getLocation();
                    System.out.printf("Dirección encontrada: %s | Coordenadas: %f,%f%n",
                            result.getGeometry(), loc.getLatitude(), loc.getLongitude());
                    return loc;
                })
                .orElseThrow(() -> new GeocodingException(
                        "No se encontraron coordenadas válidas para: " + address,
                        response.toString()));
    }

    private boolean isValidCoordinate(GeoLocation location) {
        return location.getLatitude() != null &&
                location.getLongitude() != null &&
                location.getLatitude() >= -90 && location.getLatitude() <= 90 &&
                location.getLongitude() >= -180 && location.getLongitude() <= 180;
    }
}
