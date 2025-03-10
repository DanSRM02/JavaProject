package com.oxi.software.controller;

import com.oxi.software.business.ProductVariantBusiness;
import com.oxi.software.dto.ProductVariantDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/product-variant", method = { RequestMethod.PUT, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST })
@CrossOrigin(origins = "*")
public class ProductVariantController {

    private final ProductVariantBusiness productVariantBusiness;

    @Autowired
    public ProductVariantController(ProductVariantBusiness productVariantBusiness) {
        this.productVariantBusiness = productVariantBusiness;
    }

    // Endpoint Add Product Variant
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createProductVariant(@RequestBody Map<String, Object> variantMap) {
        try {
            productVariantBusiness.add(variantMap);
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Add product variant successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error adding product variant: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint Update Product Variant
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateProductVariant(@RequestBody Map<String, Object> variantMap, @PathVariable Long id) {
        try {
            productVariantBusiness.update(variantMap, id);
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Update product variant successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error updating product variant: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint Get All Product Variants
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllProductVariants() {
        try {
            List<ProductVariantDTO> variantDTOList = productVariantBusiness.findAll();
            if (!variantDTOList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        variantDTOList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        variantDTOList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Product variants not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            throw new CustomException("Error getting product variants: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    // Endpoint Get Product Variant by ID
    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getProductVariantById(@PathVariable Long id) {
        try {
            ProductVariantDTO variantDTO = productVariantBusiness.findBy(id);
            if (variantDTO != null) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindId(
                        variantDTO,
                        ResponseHttpApi.CODE_OK,
                        "Successfully Completed"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "There isn't that product variant"
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Error getting product variant: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    // Endpoint Delete Product Variant
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteProductVariant(@PathVariable Long id) {
        try {
            productVariantBusiness.delete(id);
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Product variant deleted successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error deleting product variant: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
