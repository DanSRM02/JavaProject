package com.oxi.software.utilities.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final Instant timestamp;
    private final String error;
    private final String messageDetail;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.timestamp = Instant.now();
        this.error = status.getReasonPhrase();
        this.messageDetail = message;
    }

    /**
     * Convierte la excepción en un mapa para poder construir una respuesta JSON
     * que incluya información relevante sobre el error.
     */
    public Map<String, Object> toResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", timestamp.toString());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", messageDetail);
        return response;
    }
}
