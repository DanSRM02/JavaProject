package com.oxi.software.controller;

import com.oxi.software.business.UserBusiness;
import com.oxi.software.dto.IndividualDTO;
import com.oxi.software.dto.UserDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/user", method = { RequestMethod.PUT, RequestMethod.GET,RequestMethod.DELETE, RequestMethod.POST})
@CrossOrigin(origins = "*")
public class UserController {

    private final UserBusiness userBusiness;

    @Autowired
    public UserController(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, Object> json) {
        try {
            // Call Business to add User
            userBusiness.add(json);
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

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, Object> json, @PathVariable Long id) {
        try {
            // Call Business to update User
            userBusiness.update(json, id);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "User modified successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error modifying user: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try{
            List<UserDTO> userDTOSList = userBusiness.findAll();
            if (!userDTOSList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        userDTOSList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        userDTOSList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Individuals not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting individuals: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }
    @PostMapping(path = "/auth/login")
    public ResponseEntity<Map<String, Object>> loginUser (@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        try {
            // Llama al negocio para autenticar al usuario
            UserDTO userDTO = userBusiness.authenticate(email, password);
            if (userDTO != null) {
                // Si la autenticación es exitosa, devuelve el usuario
                Map<String, Object> response = ResponseHttpApi.responseHttpPost("Login successful", HttpStatus.OK);
                response.put("data", userDTO); // Agregar el UserDTO a la respuesta
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpPost("Invalid credentials", HttpStatus.UNAUTHORIZED),
                        HttpStatus.UNAUTHORIZED);
            }
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost("Error during login: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try{
            UserDTO userDTO = userBusiness.findBy(id);
            if (userDTO != null) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindId(
                        userDTO,
                        ResponseHttpApi.CODE_OK,
                        "Successfully Completed"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "There isn't that user"
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Error getting user: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }
}
