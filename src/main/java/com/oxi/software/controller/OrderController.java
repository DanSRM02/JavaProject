package com.oxi.software.controller;

import com.oxi.software.business.OrderBusiness;
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
@RequestMapping("/api/v1/oxi/order")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderBusiness orderBusiness;

    @Autowired
    public OrderController(OrderBusiness orderBusiness) {
        this.orderBusiness = orderBusiness;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addOrder(@RequestBody Map<String, Object> json) {
        return processRequest(() -> {

        OrderDTO response = orderBusiness.add(json);

            Long idOrder=response.getId();
            orderBusiness.sendEmail(idOrder);
            return ResponseHttpApi.responseHttpPost("Order added successfully", HttpStatus.OK);
        });
    }

    @PostMapping("/send-email/{idOrder}")
    public ResponseEntity<Map<String, Object>> sendEmail(@PathVariable Long idOrder) {
        return processRequest(() -> {

            orderBusiness.sendEmail(idOrder);
            return ResponseHttpApi.responseHttpPost("Order sent to email successfully", HttpStatus.OK);
        });
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(@RequestBody Map<String, Object> json, @PathVariable Long id) {
        return processRequest(() -> {
            orderBusiness.update(json, id);
            return ResponseHttpApi.responseHttpPost("Order modified successfully", HttpStatus.OK);
        });
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        return processRequest(() -> {
            OrderDTO orderDTO = orderBusiness.findBy(id);
            if (orderDTO != null) {
                return ResponseHttpApi.responseHttpFindId(orderDTO, ResponseHttpApi.CODE_OK, "Successfully Completed");
            }
            return ResponseHttpApi.responseHttpAction(ResponseHttpApi.CODE_BAD, "There isn't that order");
        });
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        return processRequest(() -> {
            List<OrderDTO> orderDTOList = orderBusiness.findAll();
            if (!orderDTOList.isEmpty()) {
                return ResponseHttpApi.responseHttpFindAll(orderDTOList, HttpStatus.OK, "Successfully Completed", orderDTOList.size());
            } else {
                return ResponseHttpApi.responseHttpFindAll(null, HttpStatus.NO_CONTENT, "Orders not found", 0);
            }
        });
    }

    // Método auxiliar para manejar solicitudes
    private ResponseEntity<Map<String, Object>> processRequest(RequestHandler handler) {
        try {
            return new ResponseEntity<>(handler.handle(), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Interfaz funcional para manejar lógica en processRequest
    @FunctionalInterface
    private interface RequestHandler {
        Map<String, Object> handle() throws Exception;
    }
}
