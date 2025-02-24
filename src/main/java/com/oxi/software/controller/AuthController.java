package com.oxi.software.controller;

import com.oxi.software.business.AuthBusiness;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            // Call Business to add User
            authBusiness.validateCredentials(json);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "User added successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error adding user: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
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
