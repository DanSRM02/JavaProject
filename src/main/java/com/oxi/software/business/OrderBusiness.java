package com.oxi.software.business;


import com.oxi.software.dto.DeliveryDTO;
import com.oxi.software.dto.OrderDTO;
import com.oxi.software.dto.ProductDTO;
import com.oxi.software.dto.UserDTO;
import com.oxi.software.entities.Delivery;
import com.oxi.software.entities.Order;
import com.oxi.software.entities.User;
import com.oxi.software.service.OrderService;
import com.oxi.software.service.ProductService;
import com.oxi.software.service.UserService;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderBusiness {

    private final OrderService orderService;
    private final UserService userService;

    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final ProductService productService;

    @Autowired
    public OrderBusiness(OrderService orderService, UserService userService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }

    public OrderDTO validateData(Map<String, Object> data) throws CustomException {
        //Pass Map to JSONObject
        JSONObject request = Util.getData(data);

        //Prepare DTO
        OrderDTO orderDTO = new OrderDTO();
        logger.debug(orderDTO.toString());
        System.out.println(orderDTO);

        //Assign data to DTO
        orderDTO.setId(0L);
        orderDTO.setState(request.getBoolean("state"));

        //Search user and assign to DTO
        Long userId = Long.parseLong(request.get("user_id").toString());
        UserDTO userDTO = getUserDTO(userId);
        orderDTO.setUser(userDTO);

        //The products that are in an order
        JSONArray products = request.getJSONArray("product_ids");
        List<ProductDTO> productDTOList = new ArrayList<>();

        for (int i = 0; i < products.length() ; i++) {
            JSONObject product = products.getJSONObject(i);
            Long id = Long.parseLong(product.get("id").toString());

            ProductDTO productDTO = modelMapper.map(productService.findBy(id), ProductDTO.class);
            productDTOList.add(productDTO);
        }

        orderDTO.setProductList(productDTOList);
        System.out.println(orderDTO);
        return orderDTO;
    }

    public void add(Map<String, Object> json) {
        try {
            // Validar datos y convertir a DTO
            OrderDTO orderDTO = validateData(json);

            // Crear la entidad Order y asignar propiedades
            Order order = modelMapper.map(orderDTO, Order.class);
            order.setUser(userService.findBy(orderDTO.getUser().getId()));  // Asignar el usuario

            // Guardar la orden
            this.orderService.save(order);

            // Log de información sobre la operación exitosa
            logger.info("Order added successfully: {}", order);

        } catch (CustomException ce) {
            // Log de error personalizado y relanzamiento de la excepción
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error to create order", ce.getStatus());

        } catch (Exception e) {
            // Log de error inesperado y relanzamiento de la excepción
            logger.error("Unexpected error occurred while adding order", e);
            throw new RuntimeException("Unexpected error occurred while adding order", e);
        }
    }

    public void update(Map<String, Object> json, Long id) {
        try {
            // Validar datos y convertir a DTO
            OrderDTO orderDTO = validateData(json);
            orderDTO.setId(id);  // Establecer el ID de la orden a actualizar

            // Crear la entidad Order y asignar propiedades
            Order order = modelMapper.map(orderDTO, Order.class);
            order.setUser(userService.findBy(orderDTO.getUser().getId()));  // Asignar el usuario

            // Guardar la orden actualizada
            this.orderService.save(order);

            // Log de información sobre la operación exitosa
            logger.info("Order updated successfully: {}", order);

        } catch (CustomException ce) {
            // Log de error personalizado y relanzamiento de la excepción
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error to modify order", ce.getStatus());

        } catch (Exception e) {
            // Log de error inesperado y relanzamiento de la excepción
            logger.error("Unexpected error occurred while updating order", e);
            throw new RuntimeException("Unexpected error occurred while updating order", e);
        }
    }

    public List<OrderDTO> findAll(){
        try {
            List<Order> orderList = this.orderService.findAll();
            if (orderList.isEmpty()) {
                return List.of();
            }
            return orderList.stream()
                    .map(delivery -> modelMapper.map(delivery, OrderDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting orders: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public OrderDTO findBy(Long id){
        try {
            Order order = this.orderService.findBy(id);
            logger.info("individual find {}", order);
            return modelMapper.map(order, OrderDTO.class);
        } catch (EntityNotFoundException eNT) {
            logger.error(eNT.getMessage());
            throw new CustomException("¡ERROR!, Not found user", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("¡ERROR!, Error getting user by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserDTO getUserDTO(Long id){
        User userDTO = userService.findBy(id);
        return modelMapper.map(userDTO, UserDTO.class);
    }
}
