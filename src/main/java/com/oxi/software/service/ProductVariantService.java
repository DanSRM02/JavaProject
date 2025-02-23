package com.oxi.software.service;

import com.oxi.software.entity.ProductVariant;
import com.oxi.software.repository.ProductVariantRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantService implements Idao<ProductVariant, Long> {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Override
    public ProductVariant findBy(Long id) {
        return this.productVariantRepository.findById(id).orElseThrow(()->
                new CustomException("Product Variant with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public void save(ProductVariant obje) {
        this.productVariantRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<ProductVariant> obje) {
        this.productVariantRepository.saveAll(obje);
    }

    @Override
    public void delete(ProductVariant obje) {
        this.productVariantRepository.delete(obje);
    }

    @Override
    public List<ProductVariant> findAll() {
        return this.productVariantRepository.findAll();
    }

    public boolean existsByProductAndUnit(Long productId, Long unitId) {
        return productVariantRepository.existsByProductIdAndUnitId(productId, unitId);
    }
}
