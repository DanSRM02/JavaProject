package com.oxi.software.controller;

import com.oxi.software.entities.Product;
import com.oxi.software.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryController {

    private final ProductService productService;

    @Autowired
    public InventoryController(ProductService productService) {
        this.productService = productService;
    }

    // Endpoint para crear un producto
    @PostMapping("/Create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            // Llamamos al servicio para crear el producto
            Product createdProduct = productService.createProduct(product);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Retorna el producto creado con código HTTP 201
        } catch (Exception e) {
            System.out.println("Solicitud Incorrecta");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Si hay un error, retorna 500
        }
    }
}

