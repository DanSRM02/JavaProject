package com.oxi.software.business;

import com.oxi.software.dto.DeliveryDTO;
import com.oxi.software.dto.DocumentTypeDTO;
import com.oxi.software.dto.ProductDTO;
import com.oxi.software.dto.PurchaseDTO;
import com.oxi.software.entities.Delivery;
import com.oxi.software.entities.DocumentType;
import com.oxi.software.entities.Purchase;
import com.oxi.software.service.DeliveryService;
import com.oxi.software.service.PurchaseService;
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
public class PurchaseBusiness {

    private final DeliveryService deliveryService;
    private final PurchaseService purchaseService;

    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();

    public PurchaseBusiness(DeliveryService deliveryService, PurchaseService purchaseService) {
        this.deliveryService = deliveryService;
        this.purchaseService = purchaseService;
    }

    public PurchaseDTO validateData(Map<String, Object> data) throws CustomException {
        //Pass Map to JSONObject
        JSONObject request = Util.getData(data);
        //Prepare DTO
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        //Assign data to DTO
        purchaseDTO.setId(0L);
        //Search purchase type and assing to DTO
        Long deliveryId = Long.parseLong(request.get("delivery_id").toString());
        DeliveryDTO deliveryDTO = getDeliveryDTO(deliveryId);
        purchaseDTO.setDelivery(deliveryDTO);
        return purchaseDTO;
    }

    public List<PurchaseDTO> findAll(){
        try {
            List<Purchase> productList = this.purchaseService.findAll();
            if (productList.isEmpty()) {
                return List.of();
            }
            return productList.stream()
                    .map(purchase -> modelMapper.map(purchase, PurchaseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting purchase: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public PurchaseDTO findBy(Long id) {
        try {
            Purchase purchase = this.purchaseService.findBy(id);
            logger.info("purchase find {}", purchase);
            return modelMapper.map(purchase, PurchaseDTO.class);

        } catch (EntityNotFoundException eNT) {
            logger.error(eNT.getMessage());
            throw new CustomException("¡ERROR!, Not found purchase", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("¡ERROR!, Error getting purchase by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void add(Map<String, Object> request) {
        try {
            // Validar datos y convertir a DTO
            PurchaseDTO purchaseDTO = validateData(request);

            // Crear la entidad Purchase y asignar propiedades
            Purchase purchase = new Purchase();
            purchase.setId(0L);  // Inicializar con un ID nulo (si aplica)

            // Asignar delivery
            DeliveryDTO deliveryDTO = purchaseDTO.getDelivery();
            Delivery delivery = deliveryService.findBy(deliveryDTO.getId());
            if (delivery == null) {
                throw new CustomException("Delivery not found", HttpStatus.NOT_FOUND);
            }
            purchase.setDelivery(delivery);  // Asignar Delivery a la compra

            // Calcular el total de la compra
            int total = calculateTotal(purchaseDTO);
            purchase.setTotal(total);  // Asignar el total a la compra

            // Guardar la compra
            purchaseService.save(purchase);

            logger.info("Purchase added successfully: {}", purchase);

        } catch (CustomException ce) {
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw ce;  // Lanzar la excepción personalizada para que sea manejada en la capa superior

        } catch (Exception e) {
            logger.error("Unexpected error occurred while adding purchase", e);
            throw new RuntimeException("Unexpected error occurred while adding purchase", e);  // Propagar excepción general
        }
    }

    public void update(Map<String, Object> request, Long id) {
        try {
            // Validar datos y convertir a DTO
            PurchaseDTO purchaseDTO = validateData(request);
            purchaseDTO.setId(id);  // Establecer el ID de la compra a actualizar

            // Crear la entidad Purchase y asignar propiedades
            Purchase purchase = modelMapper.map(purchaseDTO, Purchase.class);

            // Calcular el total de la compra
            int total = calculateTotal(purchaseDTO);
            purchase.setTotal(total);  // Establecer el total de la compra

            // Asignar tipo de compra y Delivery
            purchase.setDelivery(deliveryService.findBy(purchaseDTO.getDelivery().getId()));

            // Guardar la compra actualizada
            this.purchaseService.save(purchase);

            // Log información sobre la operación exitosa
            logger.info("Purchase updated successfully: {}", purchase);

        } catch (CustomException ce) {
            // Log de error personalizado y relanzamiento de la excepción
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error updating purchase", ce.getStatus());

        } catch (Exception e) {
            // Log de error inesperado y relanzamiento de la excepción
            logger.error("Unexpected error occurred while updating purchase", e);
            throw new RuntimeException("Unexpected error occurred while updating purchase", e);
        }
    }

    private int calculateTotal(PurchaseDTO purchaseDTO) {
        int total = 0;

        // Iterate over productList (assumed to be in purchaseDTO) and calculate the total
        if (purchaseDTO.getDelivery().getOrder() != null && purchaseDTO.getDelivery().getOrder().getProductList() != null) {
            for (ProductDTO product : purchaseDTO.getDelivery().getOrder().getProductList()) {
                total += product.getQuantity() * product.getPrice();  // Quantity * Price
            }
        }
        return total;
    }

    public DeliveryDTO getDeliveryDTO(Long id){
        Delivery purchaseType = deliveryService.findBy(id);
        return modelMapper.map(purchaseType, DeliveryDTO.class);
    }

}
