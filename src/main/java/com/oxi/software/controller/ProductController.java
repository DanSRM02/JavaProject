package com.oxi.software.controller;

import com.oxi.software.business.ProductBusiness;
import com.oxi.software.dto.ProductDTO;
import com.oxi.software.entities.Product;
import com.oxi.software.service.ProductService;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/product", method = {RequestMethod.GET, RequestMethod.POST})
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductBusiness productBusiness;

    @Autowired
    public ProductController(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

    // Endpoint Add Product
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Map<String, Object> productMap) {
        try {
            // Call Business to create Product
            productBusiness.add(productMap);
            // Debug for errors
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_OK,
                    "Add product successfully"),
                    HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "Error adding Product" + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody Map<String, Object> productMap, @PathVariable Long id) {
        try{
            productBusiness.update(productMap, id);
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_OK,
                    "Update product successfully"),
                    HttpStatus.OK);
        }catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "Error updated product" + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all")
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
                        "Journeys not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting Journeys: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("find/{id}")
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
            throw new CustomException("Error getting Journeys: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }
}

