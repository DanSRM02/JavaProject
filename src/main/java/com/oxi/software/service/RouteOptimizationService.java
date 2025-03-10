package com.oxi.software.service;

import com.oxi.software.dto.google.DirectionsResponse;
import com.oxi.software.dto.google.Route;
import com.oxi.software.dto.google.RouteCalculationResult;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.exception.RouteCalculationException;
import com.oxi.software.utilities.types.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteOptimizationService {
    private final RestTemplate restTemplate;

    @Value("${google.maps.api-key}")
    private String apiKey;

    public RouteCalculationResult calculateOptimalRoute(GeoLocation origin, GeoLocation destination) {
        validateCoordinates(origin);
        validateCoordinates(destination);

        // 1. Obtener respuesta de Google Maps
        DirectionsResponse response = getDirectionsFromAPI(origin, destination);

        // 2. Seleccionar mejor ruta
        Route lastCalculatedRoute = selectBestRoute(response.getRoutes());

        // 3. Construir DTO con resultados
        return new RouteCalculationResult(
                lastCalculatedRoute.getOverviewPolyline().getPoints(),
                formatDuration(lastCalculatedRoute),
                lastCalculatedRoute.getTotalDuration()
        );
    }

    private DirectionsResponse getDirectionsFromAPI(GeoLocation origin, GeoLocation destination) {
        String url = "https://maps.googleapis.com/maps/api/directions/json"
                + "?origin={originLat},{originLng}"
                + "&destination={destLat},{destLng}"
                + "&key={key}"
                + "&alternatives=true";

        Map<String, String> params = Map.of(
                "originLat", origin.getLatitude().toString(),
                "originLng", origin.getLongitude().toString(),
                "destLat", destination.getLatitude().toString(),
                "destLng", destination.getLongitude().toString(),
                "key", apiKey
        );

        DirectionsResponse response = restTemplate.getForObject(url, DirectionsResponse.class, params);

        if (response == null || !"OK".equals(response.getStatus())) {
            throw new RouteCalculationException("Error al calcular la ruta", HttpStatus.SERVICE_UNAVAILABLE);
        }

        return response;
    }

    private String formatDuration(Route route) {
        return route.getLegs().stream()
                .map(leg -> leg.getDuration().getText())
                .collect(Collectors.joining(" y "));
    }

    private Route selectBestRoute(List<Route> routes) { // Cambia el tipo de retorno a Route
        return routes.stream()
                .min(Comparator.comparingInt(Route::getTotalDuration))
                .orElseThrow(() -> new RouteCalculationException(
                        "No se encontraron rutas disponibles",
                        HttpStatus.NOT_FOUND
                ));
    }

    private void validateCoordinates(GeoLocation location) {
        if (location.getLatitude() < -90 || location.getLatitude() > 90 ||
                location.getLongitude() < -180 || location.getLongitude() > 180) {
            throw new CustomException("Coordenadas fuera de rango válido", HttpStatus.BAD_REQUEST);
        }
    }

}