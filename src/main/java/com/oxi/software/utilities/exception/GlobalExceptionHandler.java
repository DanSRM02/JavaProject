package com.oxi.software.utilities.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RouteCalculationException.class)
    public ResponseEntity<?> handleRouteCalculationException(RouteCalculationException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Map.of(
                        "error", ex.getMessage(),
                        "code", ex.getStatus().value()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(Map.of(
                        "error", "Error interno del servidor",
                        "details", ex.getMessage()
                ));
    }

    @ExceptionHandler(GeocodingException.class)
    public ResponseEntity<?> handleGeocodingError(GeocodingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Error en geocodificación",
                        "details", ex.getMessage()
                ));
    }

}