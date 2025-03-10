package com.oxi.software.utilities.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RouteCalculationException extends RuntimeException {
    private final HttpStatus status;

    public RouteCalculationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}