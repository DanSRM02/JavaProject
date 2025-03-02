package com.oxi.software.controller;

import com.oxi.software.business.AuthBusiness;
import com.oxi.software.dto.AuthResponseDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/auth", method = { RequestMethod.PUT, RequestMethod.GET,RequestMethod.DELETE, RequestMethod.POST})
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthBusiness authBusiness;

    public AuthController(AuthBusiness authBusiness) {
        this.authBusiness = authBusiness;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> json) {
        try {
            // Llamada a la capa de negocio para iniciar sesión al usuario
            AuthResponseDTO authResponseDTO = authBusiness.loginUser(json);
            String token = authResponseDTO.getToken();
            // Respuesta exitosa usando responseHttpAuth
            return new ResponseEntity<>(
                    ResponseHttpApi.responseHttpAuth(HttpStatus.OK, "User logged in successfully", token),
                    HttpStatus.OK
            );
        } catch (BadCredentialsException e) {
            // Captura la excepción de credenciales incorrectas y responde con 401 Unauthorized
            return new ResponseEntity<>(
                    ResponseHttpApi.responseHttpPost("Invalid username or password", HttpStatus.UNAUTHORIZED),
                    HttpStatus.UNAUTHORIZED
            );
        } catch (CustomException e) {
            // Respuesta para excepciones personalizadas
            return new ResponseEntity<>(
                    ResponseHttpApi.responseHttpPost(e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            // Respuesta para cualquier otra excepción
            return new ResponseEntity<>(
                    ResponseHttpApi.responseHttpPost("Error logging in: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> json) {
        try {
            // Call Business to add User
            authBusiness.register(json);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Individual registered successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error registered individual: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
