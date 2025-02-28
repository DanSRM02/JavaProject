package com.oxi.software.controller;

import com.oxi.software.business.OrderBusiness;
import com.oxi.software.dto.OrderDTO;
import com.oxi.software.dto.OrderDetailDTO;
import com.oxi.software.dto.OrderSummaryDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/order", method = { RequestMethod.PUT, RequestMethod.GET,RequestMethod.DELETE, RequestMethod.POST})
@CrossOrigin(origins = "*")
public class OrderController {
    private final OrderBusiness orderBusiness;

    @Autowired
    public OrderController(OrderBusiness orderBusiness) {
        this.orderBusiness = orderBusiness;
    }

    @PostMapping( "/add")
    public ResponseEntity<Map<String, Object>> addOrder(@RequestBody Map<String, Object> json) {
        try {
            // Call Business to add Order
            orderBusiness.add(json);

            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Order added successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error adding order: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping(path = "/send-email/{id-order}")
//    public ResponseEntity<Map<String, Object>> sendEmail(@PathVariable("id-order") Long idOrder) {
//        try {
//            // Call Business to update Order
//            orderBusiness.sendEmail(idOrder);
//            // Success response
//            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
//                    "Order send to email successfully", HttpStatus.OK),
//                    HttpStatus.OK);
//        } catch (CustomException e) {
//            // Custom exception response
//            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
//                    e.getMessage(), HttpStatus.BAD_REQUEST),
//                    HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            // General exception response
//            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
//                    "Error sending order: " + e.getMessage(), HttpStatus.BAD_REQUEST),
//                    HttpStatus.BAD_REQUEST);
//        }
//    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(@RequestBody Map<String, Object> json, @PathVariable Long id) {
        try {
            // Call Business to update Order
            orderBusiness.update(json, id);

            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Order modified successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error modifying order: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        try{
            OrderDTO orderDTO = orderBusiness.findBy(id);
            if (orderDTO != null) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindId(
                        orderDTO,
                        ResponseHttpApi.CODE_OK,
                        "Successfully Completed"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "There isn't that order"
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Error getting order: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        try{
            List<OrderDTO> orderDTOList = orderBusiness.findAll();
            if (!orderDTOList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        orderDTOList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        orderDTOList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Orders not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting orders: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all/{state}")
    public ResponseEntity<Map<String, Object>> getAllOrdersByState(@PathVariable String state) {
        try{
            List<OrderSummaryDTO> orderSummaryDTOS = orderBusiness.findAllByState(state);
            if (!orderSummaryDTOS.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        orderSummaryDTOS,
                        HttpStatus.OK,
                        "Successfully Completed",
                        orderSummaryDTOS.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Orders not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting orders: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        try{
            List<OrderDetailDTO> orderDetailDTOS = orderBusiness.findDetailsById(id);
            if (!orderDetailDTOS.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        orderDetailDTOS,
                        HttpStatus.OK,
                        "Successfully Completed",
                        orderDetailDTOS.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Order details not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error getting order details: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
