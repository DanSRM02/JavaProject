package com.oxi.software.business;


import com.oxi.software.controller.PdfService;
import com.oxi.software.dto.*;
import com.oxi.software.entities.Order;
import com.oxi.software.entities.User;
import com.oxi.software.service.MailService;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderBusiness {

    private final OrderService orderService;
    private final UserService userService;
    private final MailService mailService;
    private final PdfService pdfService;

    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final ProductService productService;

    @Autowired
    public OrderBusiness(OrderService orderService, UserService userService, MailService mailService, PdfService pdfService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.mailService = mailService;
        this.pdfService = pdfService;
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

    public PdfOrderDTO createPdfOrderDTO(OrderDTO orderDTO, int totalAmount) {

        if (orderDTO.getProductList() == null || orderDTO.getProductList().isEmpty()) {
            throw new CustomException("Product list is empty for order ID: " + orderDTO.getId(), HttpStatus.BAD_REQUEST);
        }

        return PdfOrderDTO.builder()
                .id(orderDTO.getId())
                .customerName(orderDTO.getUser().getIndividual().getName())
                .customerEmail(orderDTO.getUser().getIndividual().getEmail())
                .customerAddress(orderDTO.getUser().getIndividual().getAddress())
                .productList(orderDTO.getProductList())
                .totalAmount(totalAmount)
                .orderDate(orderDTO.getCreatedAt().toString())
                .build();
    }

    public void sendEmail(Long idOrder) {
        try {
            OrderDTO order = this.findBy(idOrder);
            if (order == null) {
                throw new CustomException("Order not found", HttpStatus.NOT_FOUND);
            }
            // Validar que el cliente tenga un correo asociado
            String customerEmail = order.getUser().getIndividual().getEmail();
            if (customerEmail == null || customerEmail.isEmpty()) {
                throw new CustomException("Customer email not found for order ID: " + idOrder, HttpStatus.BAD_REQUEST);
            }

            // Calcular el total de la orden
            int total = calculateTotal(order.getProductList());

            // Crear el PdfOrderDTO
            PdfOrderDTO pdfOrderDTO = createPdfOrderDTO(order, total);

            // Generar el PDF con PdfOrderDTO
            String pdfFilePath = pdfService.generateOrderPdf(pdfOrderDTO);

            // Verificar si el archivo PDF fue creado exitosamente
            File pdfFile = new File(pdfFilePath);
            if (!pdfFile.exists()) {
                throw new RuntimeException("Failed to generate PDF file: " + pdfFilePath);
            }

            // Crear las variables para Thymeleaf
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("order", order);
            templateVariables.put("total", total);
            templateVariables.put("pdfPath", pdfFilePath);

            // Configurar los detalles del correo (incluyendo el PDF como adjunto)
            MailDTO mailDTO = MailDTO.builder()
                    .to(customerEmail)
                    .subject("Your Order Details - Order #" + order.getId())
                    .attachment(pdfFilePath)  // Adjuntar el PDF
                    .build();

            // Log de información del envío
            logger.info("Sending email to customer: {}", customerEmail);

            // Enviar el correo con las variables de la plantilla
            mailService.sendEmailWithTemplate(mailDTO, templateVariables);

            // Log de éxito
            logger.info("Email sent successfully for order ID: {}", idOrder);

        } catch (CustomException e) {
            logger.error("Custom exception occurred: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while sending email for order ID: {}", idOrder, e);
            throw new RuntimeException("Error occurred while sending email", e);
        }
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

    public int calculateTotal(List<ProductDTO> productList) {
        if (productList == null || productList.isEmpty()) {
            return 0; // Manejo de lista nula o vacía
        }

        return productList.stream()
                .filter(product -> product.getPrice() != null && product.getQuantity() != null) // Ignorar productos inválidos
                .mapToInt(product -> product.getQuantity() * product.getPrice())
                .sum();
    }


}
