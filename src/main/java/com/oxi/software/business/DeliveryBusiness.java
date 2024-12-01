package com.oxi.software.business;

import com.oxi.software.dto.DeliveryDTO;
import com.oxi.software.dto.OrderDTO;
import com.oxi.software.dto.UserDTO;
import com.oxi.software.entities.Delivery;
import com.oxi.software.entities.Order;
import com.oxi.software.entities.User;
import com.oxi.software.service.DeliveryService;
import com.oxi.software.service.OrderService;
import com.oxi.software.service.UserService;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DeliveryBusiness {
    
    private final DeliveryService deliveryService;
    private final OrderService orderService;
    private final UserService userService;

    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();

    public DeliveryBusiness(DeliveryService deliveryService, OrderService orderService, UserService userService) {
        this.deliveryService = deliveryService;
        this.orderService = orderService;
        this.userService = userService;
    }

    public DeliveryDTO validateData(Map<String, Object> data) throws CustomException {
        //Pass Map to JSONObject
        JSONObject request = Util.getData(data);
        //Prepare DTO
        DeliveryDTO deliveryDTO = new DeliveryDTO();
        logger.debug(deliveryDTO.toString());
        System.out.println(deliveryDTO);

        //Assign data to DTO
        deliveryDTO.setId(0L);

        //Search user and assign to DTO
        Long userId = Long.parseLong(request.get("user_id").toString());
        UserDTO userDTO = getUserDTO(userId);
        deliveryDTO.setDomiciliary(userDTO);

        //Search delivery type and assign to DTO
        Long orderId = Long.parseLong(request.get("order_id").toString());
        OrderDTO orderDTO = getOrderDTO(orderId);
        deliveryDTO.setOrder(orderDTO);

        System.out.println(deliveryDTO);
        return deliveryDTO;
    }

    public List<DeliveryDTO> findAll(){
        try {
            List<Delivery> deliveryList = this.deliveryService.findAll();
            if (deliveryList.isEmpty()) {
                return List.of();
            }
            return deliveryList.stream()
                    .map(delivery -> modelMapper.map(delivery, DeliveryDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting delivery: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public DeliveryDTO findBy(Long id) {
        try {
            Delivery delivery = this.deliveryService.findBy(id);
            logger.info("delivery find {}", delivery);
            return modelMapper.map(delivery, DeliveryDTO.class);

        } catch (EntityNotFoundException eNT) {
            logger.error(eNT.getMessage());
            throw new CustomException("¡ERROR!, Not found delivery", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("¡ERROR!, Error getting delivery by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void add(Map<String, Object> request) {
        try {
            // Validar datos y convertir a DTO
            DeliveryDTO deliveryDTO = validateData(request);
            Delivery delivery = new Delivery();

            // Manejar User manualmente
            User user = userService.findBy(deliveryDTO.getDomiciliary().getId());
            if (user == null) {
                throw new CustomException("User not found", HttpStatus.NOT_FOUND);
            }

            if (user.getIndividual() == null) {
                throw new CustomException("Individual field cannot be null", HttpStatus.BAD_REQUEST);
            }

            delivery.setUser(user);

            // Manejar Order manualmente
            Order order = orderService.findBy(deliveryDTO.getOrder().getId());
            if (order == null) {
                throw new CustomException("Order not found", HttpStatus.NOT_FOUND);
            }
            delivery.setOrder(order);

            // Guardar entrega
            deliveryService.save(delivery);

            // Log de información sobre la operación exitosa
            logger.info("Delivery added successfully: {}", delivery);

        } catch (CustomException ce) {
            // Log de error personalizado y relanzamiento de la excepción
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error adding delivery: " + ce.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            // Log de error inesperado y relanzamiento de la excepción
            logger.error("Unexpected error occurred while adding delivery", e);
            throw new RuntimeException("Unexpected error occurred while adding delivery", e);
        }
    }

    public void update(Map<String, Object> request, Long id) {
        try {
            // Validar datos y convertir a DTO
            DeliveryDTO deliveryDTO = validateData(request);
            Delivery delivery = new Delivery();

            // Establecer el ID de la entrega a actualizar
            delivery.setId(id);

            // Manejar User manualmente
            User user = userService.findBy(deliveryDTO.getDomiciliary().getId());
            if (user == null) {
                throw new CustomException("User not found", HttpStatus.NOT_FOUND);
            }
            user.setIndividual(user.getIndividual());
            delivery.setUser(user);

            // Manejar Order manualmente
            Order order = orderService.findBy(deliveryDTO.getOrder().getId());
            if (order == null) {
                throw new CustomException("Order not found", HttpStatus.NOT_FOUND);
            }
            delivery.setOrder(order);

            // Guardar la entrega actualizada
            this.deliveryService.save(delivery);

            // Log de información sobre la operación exitosa
            logger.info("Delivery updated successfully: {}", delivery);

        } catch (CustomException ce) {
            // Log de error personalizado y relanzamiento de la excepción
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error updating delivery", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            // Log de error inesperado y relanzamiento de la excepción
            logger.error("Unexpected error occurred while updating delivery", e);
            throw new RuntimeException("Unexpected error occurred while updating delivery", e);
        }
    }

    public UserDTO getUserDTO(Long id){
        User user = userService.findBy(id);
        return modelMapper.map(user, UserDTO.class);
    }

    public OrderDTO getOrderDTO(Long id){
        Order order = orderService.findBy(id);
        return modelMapper.map(order, OrderDTO.class);
    }
}
