package com.oxi.software.service;

import com.oxi.software.entities.Product;
import com.oxi.software.repository.ProductRepository;
import com.oxi.software.service.dao.Idao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements Idao<Product, Long> {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product getById(Long aLong) {
        return this.productRepository.findById(aLong).orElse(null);  // Cambiado por findById
    }

    @Override
    public void save(Product obje) {
        this.productRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<Product> obje) {
        this.productRepository.saveAll(obje);
    }

    @Override
    public void delete(Product obje) {
        this.productRepository.delete(obje);
    }

    @Override
    public List<Product> findAll() {
        return this.productRepository.findAll();
    }

    public Product createProduct(Product product) {

        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        // Guarda el producto si todas las validaciones son correctas
        return this.productRepository.save(product);
    }
}
