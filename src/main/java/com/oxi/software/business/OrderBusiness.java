package com.oxi.software.business;

import com.oxi.software.dto.*;
import com.oxi.software.entity.*;
import com.oxi.software.repository.projection.OrderDetailsProjection;
import com.oxi.software.repository.projection.OrderSummaryProjection;
import com.oxi.software.service.OrderService;
import com.oxi.software.service.ProductService;
import com.oxi.software.service.ProductVariantService;
import com.oxi.software.service.UserService;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final ProductVariantService productVariantService;

    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();

    public OrderBusiness(OrderService orderService, UserService userService, ProductService productService, ProductVariantService productVariantService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productVariantService = productVariantService;
    }

    /**
     * Valida el JSON recibido y construye un OrderDTO con líneas de orden.
     * Se espera que en el JSON exista "user_id" y un array "product_ids" con objetos que contengan "id" y "quantity".
     */
    public OrderDTO validateData(Map<String, Object> data) throws CustomException {
        // Convertir el mapa a JSONObject
        JSONObject request = Util.getData(data);

        // Preparar DTO de Order
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(0L);
        orderDTO.setState("PENDING");
        orderDTO.setPriority(false);

        // Buscar usuario y asignarlo
        Long userId = Long.parseLong(request.get("user_id").toString());
        UserDTO userDTO = getUserDTO(userId);
        orderDTO.setUser(userDTO);

        JSONArray products = request.getJSONArray("product_ids");
        List<OrderLineDTO> orderLines = new ArrayList<>();

        for (int i = 0; i < products.length(); i++) {
            JSONObject productJson = products.getJSONObject(i);
            Long productId = productJson.getLong("id");
            int quantity = productJson.optInt("quantity", 1);

            // Busca el producto y/o variante
            ProductVariant product = productVariantService.findBy(productId);
            if (product == null) {
                throw new CustomException("Producto no encontrado con ID: " + productId, HttpStatus.NOT_FOUND);
            }

            // Imprime el producto en consola
            System.out.println("Producto encontrado:");
            System.out.println("ID: " + product.getId());
            System.out.println("Precio: " + product.getPrice());


            OrderLineDTO line = OrderLineDTO.builder()
                    .productVariant(modelMapper.map(product, ProductVariantDTO.class))
                    .quantity(quantity)
                    .build();
            orderLines.add(line);
        }

        orderDTO.setOrderLines(orderLines);

        // Inicialmente el total se asigna en 0, se calculará en el method add
        orderDTO.setTotal(0.0);

        logger.debug("OrderDTO after validation: {}", orderDTO);
        return orderDTO;
    }

    public void add(Map<String, Object> json) {
        try {
            // 1. Validar datos y convertir a OrderDTO
            OrderDTO orderDTO = validateData(json);
            // Para una nueva orden, el ID debe ser null
            orderDTO.setId(null);

            // 2. Mapear el DTO a entidad Order y asignar el usuario
            Order order = modelMapper.map(orderDTO, Order.class);
            order.setUser(userService.findBy(orderDTO.getUser().getId()));
            order.setState("PENDING");
            order.setPriority(false);

            // 3. Inicializar total en 0
            double total = 0.0;

            // 4. Crear la lista de líneas de orden (OrderLine)
            List<OrderLine> orderLines = new ArrayList<>();

            // Recorremos cada línea de la orden en el DTO
            for (OrderLineDTO lineDTO : orderDTO.getOrderLines()) {
                // Obtenemos el ID del productVariant en lugar del product
                Long productVariantId = lineDTO.getProductVariant().getId();
                ProductVariant variant = productVariantService.findBy(productVariantId);

                if (variant == null) {
                    throw new CustomException("No se encontró la variante con ID: " + productVariantId, HttpStatus.NOT_FOUND);
                }

                int quantityRequested = lineDTO.getQuantity();
                if (variant.getQuantity() < quantityRequested) {
                    throw new CustomException("Stock insuficiente para la variante con ID: " + productVariantId, HttpStatus.BAD_REQUEST);
                }

                // Calcular subtotal y acumular en total
                double subtotal = variant.getPrice() * quantityRequested;
                total += subtotal;

                // Restar stock y guardar la variante actualizada
                variant.setQuantity(variant.getQuantity() - quantityRequested);
                productVariantService.save(variant);

                // Crear la entidad OrderLine
                OrderLine line = new OrderLine();
                line.setOrder(order);
                line.setProductVariant(variant);
                line.setQuantity(quantityRequested);

                // Agregar la línea a la lista
                orderLines.add(line);
            }

            // 5. Asignar el total calculado a la orden
            order.setTotal(total);

            // 6. Asignar la lista de líneas a la orden
            // (suponiendo que en la entidad Order tengas un campo: private List<OrderLine> orderLines)
            order.setOrderLines(orderLines);

            // 7. Guardar la orden (esto persistirá también las líneas)
            orderService.save(order);

            logger.info("Order added successfully: {}", order);

        } catch (DataIntegrityViolationException ex) {
            logger.error("Data integrity violation: {}", ex.getMessage(), ex);
            throw new CustomException("Ya existe una orden con datos conflictivos", HttpStatus.BAD_REQUEST);
        } catch (CustomException ce) {
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error al crear la orden: " + ce.getMessage(), ce.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while adding order: {}", e.getMessage(), e);
            throw new CustomException("Error inesperado al crear la orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public List<OrderSummaryDTO> findAllByState(String state) {
        try {
            List<OrderSummaryProjection> orderList = orderService.findAllByState(state);
            if (orderList.isEmpty()) {
                return List.of();
            }
            return orderList.stream()
                    .map(order -> modelMapper.map(order, OrderSummaryDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error al obtener órdenes por estado: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<OrderDetailDTO> findDetailsById(Long id) {
        try {

            if (id == null) {
             throw new CustomException("Id null", HttpStatus.BAD_REQUEST);
            }

            List<OrderDetailsProjection> orderList = orderService.findOrderDetailsById(id);
            if (orderList.isEmpty()) {
                return List.of();
            }
            return orderList.stream()
                    .map(order -> modelMapper.map(order, OrderDetailDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void changeStatus(Map<String, Object> data, Long id) {
        try{
            Order order = orderService.findBy(id);
            //Pass Map to JSONObject
            JSONObject request = Util.getData(data);

            System.out.println(request);

            if (order == null) {
                throw new CustomException("Order not found", HttpStatus.NOT_FOUND);
            }



            order.setState(request.getString("state"));
            orderService.save(order);

        } catch (Exception e) {
            throw new CustomException("Error getting Order: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public List<OrderDTO> findOrdersByUserId(Long userId) {
        try{
            if (userId == null) {
                throw new CustomException("user id must be not null", HttpStatus.BAD_REQUEST);
            }
            List<Order> orderList = orderService.getOrdersByUser(userId);
            if (orderList.isEmpty()) {
                return List.of();
            }
            return orderList.stream()
                    .map(order -> modelMapper.map(order, OrderDTO.class))
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Map<String, Object> json, Long id) {
        try {
            OrderDTO orderDTO = validateData(json);

            orderDTO.setId(id);
            Order order = modelMapper.map(orderDTO, Order.class);
            order.setUser(userService.findBy(orderDTO.getUser().getId()));
            orderService.save(order);
            logger.info("Order updated successfully: {}", order);
        } catch (CustomException ce) {
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error al modificar la orden: " + ce.getMessage(), ce.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while updating order: {}", e.getMessage(), e);
            throw new CustomException("Error inesperado al actualizar la orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<OrderDTO> findAll(){
        try {
            List<Order> orderList = orderService.findAll();
            if (orderList.isEmpty()) {
                return List.of();
            }
            return orderList.stream()
                    .map(order -> modelMapper.map(order, OrderDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error al obtener órdenes: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public OrderDTO findBy(Long id){
        try {
            Order order = orderService.findBy(id);
            logger.info("Order found: {}", order);
            return modelMapper.map(order, OrderDTO.class);
        } catch (EntityNotFoundException eNT) {
            logger.error(eNT.getMessage());
            throw new CustomException("Orden no encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("Error al obtener la orden por ID", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserDTO getUserDTO(Long id){
        User user = userService.findBy(id);
        return modelMapper.map(user, UserDTO.class);
    }
}
