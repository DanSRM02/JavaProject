package com.oxi.software.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.maps.routing.v2.*;
import com.google.protobuf.util.Durations;
import com.google.protobuf.util.JsonFormat;
import com.oxi.software.dto.google.GeoLocation;
import com.oxi.software.dto.google.RouteCalculationResult;
import com.oxi.software.utilities.exception.RouteCalculationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class RouteOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizationService.class);

    @Value("${google.application.credentials}")
    private String credentialsPath;

    public RouteCalculationResult calculateOptimalRoute(GeoLocation origin, GeoLocation destination) {
        logger.info("Iniciando cálculo de ruta entre {} y {}",
                formatCoordinates(origin), formatCoordinates(destination));

        try {
            // Cargar credenciales de la cuenta de servicio
            GoogleCredentials credentials;
            try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
                credentials = GoogleCredentials.fromStream(serviceAccountStream)
                        .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
                logger.debug("Credenciales cargadas correctamente");
            }

            // Configurar el cliente de Routes incluyendo el header de FieldMask en todas las llamadas
            RoutesSettings settings = RoutesSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    // Agregamos el header 'x-goog-fieldmask' con el valor requerido
                    .setHeaderProvider(() ->
                            Map.of("x-goog-fieldmask", "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline"))
                    .build();

            try (RoutesClient client = RoutesClient.create(settings)) {
                logger.info("Cliente Routes configurado exitosamente");

                // Construir la solicitud
                ComputeRoutesRequest request = ComputeRoutesRequest.newBuilder()
                        .setOrigin(convertToWaypoint(origin))
                        .setDestination(convertToWaypoint(destination))
                        .setTravelMode(RouteTravelMode.DRIVE)
                        .setRoutingPreference(RoutingPreference.TRAFFIC_AWARE)
                        .setLanguageCode("en-US")
                        .setUnits(Units.IMPERIAL)
                        .setRouteModifiers(buildRouteModifiers())
                        .build();

                logger.debug("Solicitud construida:\n{}", JsonFormat.printer().print(request));

                // Enviar la solicitud y obtener la respuesta
                long startTime = System.currentTimeMillis();
                ComputeRoutesResponse response = client.computeRoutes(request);
                logger.info("Respuesta recibida en {} ms", System.currentTimeMillis() - startTime);
                logger.debug("Respuesta cruda:\n{}", JsonFormat.printer().print(response));

                if (response.getRoutesCount() == 0) {
                    logger.warn("No se encontraron rutas válidas");
                    throw new RouteCalculationException("No se encontraron rutas", HttpStatus.NOT_FOUND);
                }

                Route bestRoute = response.getRoutes(0);
                logger.debug("Mejor ruta seleccionada:\n{}", JsonFormat.printer().print(bestRoute));

                return new RouteCalculationResult(
                        bestRoute.getPolyline().getEncodedPolyline(),
                        formatDuration(bestRoute.getDuration()),
                        (int) bestRoute.getDistanceMeters()
                );
            } catch (Exception e) {
                logger.error("Error en servicio de rutas: {}", e.getMessage(), e);
                throw new RouteCalculationException("Error en servicio de rutas: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            logger.error("Error de E/S: {}", e.getMessage(), e);
            throw new RouteCalculationException("Error de conexión: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    // Métodos auxiliares

    private RouteModifiers buildRouteModifiers() {
        RouteModifiers modifiers = RouteModifiers.newBuilder()
                .setAvoidTolls(false)
                .setAvoidHighways(false)
                .setAvoidFerries(false)
                .build();
        logger.debug("Modificadores de ruta: {}", modifiers);
        return modifiers;
    }

    private Waypoint convertToWaypoint(GeoLocation location) {
        return Waypoint.newBuilder()
                .setLocation(Location.newBuilder()
                        .setLatLng(com.google.type.LatLng.newBuilder()
                                .setLatitude(location.getLatitude())
                                .setLongitude(location.getLongitude()))
                )
                .build();
    }

    private String formatCoordinates(GeoLocation location) {
        return String.format("[Lat: %.6f, Lng: %.6f]", location.getLatitude(), location.getLongitude());
    }

    private String formatDuration(com.google.protobuf.Duration duration) {
        long seconds = Durations.toSeconds(duration);
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours > 0 ? hours + " h " + minutes + " min" : minutes + " min";
    }
}
