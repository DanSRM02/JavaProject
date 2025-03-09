package com.oxi.software.business;

import com.oxi.software.dto.ProductDTO;
import com.oxi.software.dto.ProductVariantDTO;
import com.oxi.software.dto.UnitDTO;
import com.oxi.software.entity.Product;
import com.oxi.software.entity.ProductVariant;
import com.oxi.software.entity.Unit;
import com.oxi.software.service.ProductService;
import com.oxi.software.service.ProductVariantService;
import com.oxi.software.service.UnitService;
import com.oxi.software.utilities.types.Util;
import com.oxi.software.utilities.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductVariantBusiness {

    private final ProductVariantService productVariantService;
    private final UnitService unitService;
    private final ProductService productService;
    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();

    public ProductVariantBusiness(ProductVariantService productVariantService, UnitService unitService, ProductService productService) {
        this.productVariantService = productVariantService;
        this.unitService = unitService;
        this.productService = productService;
    }

    /**
     * Valida y convierte el mapa de datos a un ProductVariantDTO.
     * Se espera que el JSON contenga "quantity", "price", "unit_id" y "product_id".
     */
    public ProductVariantDTO validateData(Map<String, Object> data) throws CustomException {
        // Convertir el mapa a JSONObject
        JSONObject request = Util.getData(data);

        // Preparar el DTO de variante
        ProductVariantDTO variantDTO = new ProductVariantDTO();

        // Obtener el ID de la unidad desde el request
        Long unitId = request.getLong("unit_id");
        UnitDTO unitDTO = getUnitDto(unitId);

        // Obtener el ID del producto desde el request
        Long productId = request.getLong("product_id");
        ProductDTO productDTO = getProductDTO(productId);

        // Asignar datos al DTO
        variantDTO.setId(0L);
        variantDTO.setQuantity(request.getInt("quantity"));
        variantDTO.setPrice(request.getInt("price"));
        variantDTO.setUnit(unitDTO);
        variantDTO.setProduct(productDTO);

        return variantDTO;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.toResponse(), ex.getStatus());
    }

    public List<ProductVariantDTO> findAll() {
        try {
            List<ProductVariant> variantList = this.productVariantService.findAll();
            if (variantList.isEmpty()) {
                return List.of();
            }
            return variantList.stream()
                    .map(variant -> modelMapper.map(variant, ProductVariantDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting product variants: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void delete(Long id) throws CustomException {
        try {
            // Verificar si la variante existe
            ProductVariant variant = productVariantService.findBy(id);
            if (variant == null) {
                throw new CustomException("Product variant not found with ID: " + id, HttpStatus.NOT_FOUND);
            }
            productVariantService.delete(variant);
            logger.info("Product variant deleted successfully with ID: {}", id);
        } catch (CustomException ce) {
            logger.error("Custom error occurred while deleting product variant: {}", ce.getMessage(), ce);
            throw new CustomException("Error deleting product variant: " + ce.getMessage(), ce.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while deleting product variant", e);
            throw new CustomException("Unexpected error occurred while deleting product variant: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ProductVariantDTO findBy(Long id) {
        try {
            ProductVariant variant = this.productVariantService.findBy(id);
            return modelMapper.map(variant, ProductVariantDTO.class);
        } catch (CustomException ce) {
            logger.error("Error finding product variant: {}", ce.getMessage(), ce);
            throw new CustomException("Error finding product variant: " + ce.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while finding product variant: {}", e.getMessage(), e);
            throw new CustomException("Unexpected error occurred while finding product variant: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void add(Map<String, Object> requestData) {
        try {
            // 1. Validar datos y convertir a DTO
            ProductVariantDTO variantDTO = validateData(requestData);

            // 2. Verificar si ya existe una variante con la misma combinación de producto y unidad
            Long productId = variantDTO.getProduct().getId();
            Long unitId = variantDTO.getUnit().getId();

            if (productVariantService.existsByProductAndUnit(productId, unitId)) {
                throw new CustomException("Ya existe una variante para este producto con esa unidad", HttpStatus.BAD_REQUEST);
            }

            // 3. Convertir DTO a entidad
            ProductVariant variant = modelMapper.map(variantDTO, ProductVariant.class);

            // 4. Guardar la variante
            productVariantService.save(variant);
            logger.info("Product variant added successfully: {}", variant);

        } catch (DataIntegrityViolationException ex) {
            // Manejo de la violación de la restricción única a nivel de BD
            logger.error("Data integrity violation: {}", ex.getMessage(), ex);
            throw new CustomException("Ya existe una variante para este producto con esa unidad", HttpStatus.BAD_REQUEST);
        } catch (CustomException ce) {
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error adding product variant: " + ce.getMessage(), ce.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while adding product variant: {}", e.getMessage(), e);
            throw new CustomException("Unexpected error occurred while adding product variant: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void update(Map<String, Object> requestData, Long id) {
        try {
            // Validar datos y convertir a DTO
            ProductVariantDTO variantDTO = validateData(requestData);
            variantDTO.setId(id); // Asignar el ID para actualizar

            // Convertir DTO a entidad
            ProductVariant variant = modelMapper.map(variantDTO, ProductVariant.class);

            // Guardar la variante actualizada
            productVariantService.save(variant);
            logger.info("Product variant updated successfully: {}", variant);
        } catch (CustomException ce) {
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error updating product variant: " + ce.getMessage(), ce.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while updating product variant: {}", e.getMessage(), e);
            throw new CustomException("Unexpected error occurred while updating product variant: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UnitDTO getUnitDto(Long id) {
        Unit unit = unitService.findBy(id);
        return modelMapper.map(unit, UnitDTO.class);
    }

    private ProductDTO getProductDTO(Long id) {
        Product product = productService.findBy(id);
        return modelMapper.map(product, ProductDTO.class);
    }
}