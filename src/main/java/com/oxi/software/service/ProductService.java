package com.oxi.software.service;

import com.oxi.software.entities.Product;
import com.oxi.software.service.dao.Idao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements Idao<Product, Long> {
    @Override
    public Product getById(Long aLong) {
        return null;
    }

    @Override
    public void save(Product obje) {

    }

    @Override
    public void saveAll(Iterable<Product> obje) {

    }

    @Override
    public void delete(Product obje) {

    }

    @Override
    public List<Product> findAll() {
        return List.of();
    }
}
