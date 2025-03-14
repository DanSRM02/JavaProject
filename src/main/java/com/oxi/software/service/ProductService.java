package com.oxi.software.service;

import com.oxi.software.entity.Product;
import com.oxi.software.repository.ProductRepository;
import com.oxi.software.repository.projection.ProductDetailProjection;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductService implements Idao<Product, Long> {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product findBy(Long id) {
        return this.productRepository.findById(id).orElseThrow(()->
            new CustomException("Product with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public Product save(Product obje) {
        this.productRepository.save(obje);
        return obje;
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

    public List<ProductDetailProjection> findProductDetailsByProductId(Set<Long> ordersId) {
        return this.productRepository.findProductsByOrderIds(ordersId);
    }
}
