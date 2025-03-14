package com.oxi.software.controller;

import com.oxi.software.business.ProductBusiness;
import com.oxi.software.dto.ProductDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/product", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductBusiness productBusiness;

    @Autowired
    public ProductController(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Map<String, Object> productMap) {
        try {
            // Call Business to create Product
            productBusiness.add(productMap);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Product added successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error adding product: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/toggler/{id}")
    public ResponseEntity<Map<String, Object>> toggleProduct(@RequestBody Map<String, Object> product , @PathVariable("id") Long id) {
        try {
            // Call Business to update Product
            productBusiness.changeStatus(product, id);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Product updated successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error updating product: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody Map<String, Object> productMap, @PathVariable Long id) {
        try {
            // Call Business to update Product
            productBusiness.update(productMap, id);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Product updated successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error updating product: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        try{
            List<ProductDTO> productDTOList = productBusiness.findAll();
            if (!productDTOList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        productDTOList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        productDTOList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Products not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting Products: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        try{
            ProductDTO productDTO = productBusiness.findBy(id);
            if (productDTO != null) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindId(
                        productDTO,
                        ResponseHttpApi.CODE_OK,
                        "Successfully Completed"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "There isn't that product"
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Error getting Product: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteProductById(@PathVariable Long id) {
        try {
            // Intentar eliminar el producto
            productBusiness.delete(id);

            // Si el producto no se encuentra, lanzamos una excepción para indicar que no fue encontrado
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_OK,
                    "Product deleted successfully"
            ), HttpStatus.NO_CONTENT); // Devolver 204 No Content si se eliminó exitosamente

        } catch (CustomException ex) {
            // Si no se encuentra el producto, manejamos la excepción personalizada
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "Product not found with ID: " + id
            ), HttpStatus.NOT_FOUND); // Devolver 404 si no se encontró el producto

        } catch (Exception e) {
            // Manejo de otros errores inesperados
            throw new CustomException("Error deleting Product: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}

