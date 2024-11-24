package com.oxi.software.controller;


import com.oxi.software.business.UserBusiness;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/user",
        method = {
        RequestMethod.PUT,
                RequestMethod.GET,RequestMethod.DELETE, RequestMethod.POST})
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserBusiness userBusiness;
    @Autowired
    private Util util;

    @PostMapping(path = "/add")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, Object> json) {
        try {
            userBusiness.add(json);
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_OK,
                    "User added successfully"),
                    HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error adding environment: " + e.getMessage(),
                    HttpStatus.CONFLICT),
                    HttpStatus.CONFLICT);
        }
    }
}
