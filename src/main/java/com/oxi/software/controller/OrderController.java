package com.oxi.software.controller;

import com.oxi.software.business.OrderBusiness;
import com.oxi.software.dto.DeliveryDTO;
import com.oxi.software.dto.OrderDTO;
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

    @PostMapping(path = "/add")
    public ResponseEntity<Map<String, Object>> addOrder(@RequestBody Map<String, Object> json) {
        try {
            orderBusiness.add(json);
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_OK,
                    "Order added successfully"),
                    HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "Error adding Order" + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<Map<String, Object>> addOrder(@RequestBody Map<String, Object> json, @PathVariable Long id) {
        try {
            orderBusiness.update(json, id);
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_OK,
                    "Order modified successfully"),
                    HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "Error modified order" + e.getMessage()),
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
    public ResponseEntity<Map<String, Object>> getAllDeliverys() {
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
                        "Deliverys not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting deliverys: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }
}
