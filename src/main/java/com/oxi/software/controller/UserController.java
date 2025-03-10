package com.oxi.software.controller;

import com.oxi.software.business.UserBusiness;
import com.oxi.software.dto.DeliveryProDTO;
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
    @PutMapping(path = "/{id}/new-address")
    public ResponseEntity<Map<String, Object>> updateAddress(@RequestBody Map<String, Object> json, @PathVariable Long id) {
        try{
            userBusiness.assignAddress(json, id);

            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "New Address created successfully", HttpStatus.OK),
                    HttpStatus.OK);
        }catch (Exception e){
            throw new CustomException("Error getting individuals: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/deliveries/active")
    public ResponseEntity<Map<String, Object>> findActiveDeliveries() {
        try{
            List<DeliveryProDTO> deliveryProDTOS = userBusiness.findActiveDeliveries();
            if (!deliveryProDTOS.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        deliveryProDTOS,
                        HttpStatus.OK,
                        "Successfully Completed",
                        deliveryProDTOS.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Users not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting individuals: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/toggler/{id}")
    public ResponseEntity<Map<String, Object>> toggleProduct(@RequestBody Map<String, Object> status , @PathVariable("id") Long id) {
        try {
            // Call Business to update Product
            userBusiness.changeStatus(status, id);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Product updated successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error updating product: " + e.getMessage(), HttpStatus.BAD_REQUEST),
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
                        "Users not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting individuals: " + e.getMessage(),
                    HttpStatus.CONFLICT);
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
