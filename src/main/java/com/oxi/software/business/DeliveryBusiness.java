package com.oxi.software.business;

import com.oxi.software.dto.*;
import com.oxi.software.dto.google.DeliveryResponse;
import com.oxi.software.dto.google.RouteCalculationResult;
import com.oxi.software.entity.Delivery;
import com.oxi.software.entity.Order;
import com.oxi.software.entity.User;
import com.oxi.software.repository.projection.DeliveryBasicProjection;
import com.oxi.software.repository.projection.ProductDetailProjection;
import com.oxi.software.service.*;
import com.oxi.software.utilities.exception.GeocodingException;
import com.oxi.software.utilities.types.GeoLocation;
import com.oxi.software.utilities.types.Util;
import com.oxi.software.utilities.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DeliveryBusiness {
    
    private final DeliveryService deliveryService;
    private final OrderService orderService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final RouteOptimizationService routeOptimizationService;

    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final ProductService productService;

    public DeliveryBusiness(DeliveryService deliveryService, OrderService orderService, UserService userService, NotificationService notificationService, Util util, RouteOptimizationService routeOptimizationService, ProductService productService) {
        this.deliveryService = deliveryService;
        this.orderService = orderService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.routeOptimizationService = routeOptimizationService;
        this.productService = productService;
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
        Long userId = Long.parseLong(request.get("domiciliary_id").toString());
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

    @Transactional
    public DeliveryResponse startDelivery(Long deliveryId, Map<String, Object> request) {
        // 1. Validar entrega
        Delivery delivery = deliveryService.findBy(deliveryId);

        // 2. Parsear y validar coordenadas
        StartDeliveryDTO startDeliveryDTO = parseStartDeliveryRequest(request);

        // 3. Validar estado
        if (!Objects.equals(delivery.getState(), "READY_TO_DISPATCH")) {
            throw new IllegalStateException("La entrega no está lista para despacho");
        }

        // 4. Obtener ubicaciones
        GeoLocation origin = resolveOrigin(delivery.getDomiciliary(), startDeliveryDTO);
        GeoLocation destination = resolveDestination(delivery);

        // 5. Calcular ruta
        RouteCalculationResult routeResult = routeOptimizationService.calculateOptimalRoute(origin, destination);

        // 6. Actualizar entrega
        updateDeliveryState(delivery, routeResult);

        // 7. Enviar notificación
        notificationService.sendDeliveryNotification(delivery);

        // 8. Construir respuesta
        return buildDeliveryResponse(delivery, routeResult);
    }

    // Métodos auxiliares
    private StartDeliveryDTO parseStartDeliveryRequest(Map<String, Object> request) {
        JSONObject data = Util.getData(request);
        StartDeliveryDTO dto = new StartDeliveryDTO();
        dto.setLat(data.getDouble("lat"));
        dto.setLng(data.getDouble("lng")); // Clave en minúscula
        return dto;
    }

    private GeoLocation resolveOrigin(User domiciliary, StartDeliveryDTO dto) {
        if (dto.getLat() != null && dto.getLng() != null) {
            return new GeoLocation(dto.getLat(), dto.getLng());
        }
        return domiciliary.getIndividual().getFixedLocations().stream()
                .findFirst()
                .orElseThrow(() -> new CustomException("Configure ubicación predeterminada", HttpStatus.BAD_REQUEST));
    }

    private GeoLocation resolveDestination(Delivery delivery) {
        try {
            return delivery.getOrder().getUser().getIndividual().toGeoLocation();
        } catch (GeocodingException e) {
            throw new CustomException("Dirección del cliente inválida: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void updateDeliveryState(Delivery delivery, RouteCalculationResult routeResult) {
        delivery.setState("IN_TRANSIT");
        delivery.setStartTime(LocalDateTime.now());
        delivery.setOptimizedRoute(routeResult.getPolyline());
        deliveryService.save(delivery);
    }

    private DeliveryResponse buildDeliveryResponse(Delivery delivery, RouteCalculationResult routeResult) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .state(delivery.getState())
                .optimizedRoute(routeResult.getPolyline())
                .startTime(delivery.getStartTime())
                .estimatedDuration(routeResult.getFormattedDuration())
                .build();
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
            // 1. Validación mejorada de datos
            DeliveryDTO deliveryDTO = validateData(request);
            Delivery delivery = new Delivery();

            // 2. Validación de usuario domiciliario
            User domiciliary = userService.findBy(deliveryDTO.getDomiciliary().getId());
            validateDomiciliary(domiciliary); // Nueva validación de rol

            // 3. Validación del estado de la orden
            Order order = orderService.findBy(deliveryDTO.getOrder().getId());
            validateOrderState(order); // Nueva validación de estado de orden

            // 4. Configuración de la entrega
            delivery.setDomiciliary(domiciliary);
            delivery.setOrder(order);
            delivery.setState("PENDING"); // Estado inicial consistente con frontend

            // 5. Transacción atómica
            executeDeliveryCreation(delivery, order); // Manejo transaccional

            logger.info("Delivery creado exitosamente - ID: {} | Domiciliario: {} | Orden: {}",
                    delivery.getId(), domiciliary.getId(), order.getId());

        } catch (CustomException ce) {

            throw ce; // Excepción específica para el cliente

        } catch (Exception e) {
            logger.error("Error crítico en creación de delivery", e);
            throw new CustomException("Error interno del sistema",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Métodos de apoyo
    private void validateDomiciliary(User user) {
        if (user == null) {
            throw new CustomException("Domiciliario no encontrado",
                    HttpStatus.NOT_FOUND
            );
        }
        if (!"DOMICILIARIO".equals(user.getRolType().getName())) {
            throw new CustomException("El usuario no es un domiciliario válido",
                    HttpStatus.BAD_REQUEST );
        }
    }

    private void validateOrderState(Order order) {
        if (order == null) {
            throw new CustomException("Orden no encontrada",
                    HttpStatus.NOT_FOUND);
        }
        if (!"APPROVED".equals(order.getState())) {
            throw new CustomException("La orden no está en estado aprobado",
                    HttpStatus.CONFLICT);
        }
    }

    @Transactional
    protected void executeDeliveryCreation(Delivery delivery, Order order) {
        orderService.save(order);
        delivery.setState("READY_TO_DISPATCH"); // Actualización del estado de la orden
        deliveryService.save(delivery);
    }

    public List<DeliveryDetailsDTO> findDeliveryById(Long deliveryId) {
        try {
            // 1. Obtener datos básicos
            List<DeliveryBasicProjection> deliveries = deliveryService.findDeliveryDetails(deliveryId);

            // 2. Extraer orderIds únicos
            Set<Long> orderIds = deliveries.stream()
                    .map(DeliveryBasicProjection::getOrderId)
                    .collect(Collectors.toSet());

            // 3. Obtener productos agrupados por orderId
            Map<Long, List<ProductDetailProjection>> productsByOrder = productService
                    .findProductDetailsByProductId(orderIds)
                    .stream()
                    .collect(Collectors.groupingBy(ProductDetailProjection::getOrderId));


            // 4. Combinar proyecciones en el DTO
            return deliveries.stream().map(delivery -> {
                DeliveryDetailsDTO dto = new DeliveryDetailsDTO();

                // Mapear campos básicos
                dto.setDeliveryId(delivery.getDeliveryId());
                dto.setDeliveryState(delivery.getDeliveryState());
                dto.setCreatedAt(delivery.getCreatedAt());
                dto.setOrderId(delivery.getOrderId());
                dto.setOrderState(delivery.getOrderState());
                dto.setOrderTotal(delivery.getOrderTotal());
                dto.setPriority(delivery.getPriority());
                dto.setClientName(delivery.getClientName());
                dto.setClientAddress(delivery.getClientAddress());
                dto.setClientPhone(delivery.getClientPhone());

                // Mapear productos
                List<DeliveryDetailsDTO.ProductDetailDTO> products = productsByOrder
                        .getOrDefault(delivery.getOrderId(), Collections.emptyList())
                        .stream()
                        .map(p -> DeliveryDetailsDTO.ProductDetailDTO.builder()
                                .productName(p.getProductName())
                                .quantityOrdered(p.getQuantityOrdered())
                                .unitAcronym(p.getUnitAcronym())
                                .unitType(p.getUnitType())
                                .unitPrice(p.getUnitPrice())
                                .build())
                        .collect(Collectors.toList());

                dto.setProducts(products);
                return dto;
            }).collect(Collectors.toList());

        } catch (CustomException ce) {
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error obteniendo detalles", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error inesperado: ", e);
            throw new RuntimeException("Error crítico al procesar entregas", e);
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
            delivery.setDomiciliary(user);

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

