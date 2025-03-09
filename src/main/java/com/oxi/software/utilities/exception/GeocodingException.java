package com.oxi.software.utilities.exception;

import org.springframework.http.HttpStatus;

public class GeocodingException extends RuntimeException {

    public GeocodingException(String status, String address) {
    }
}
