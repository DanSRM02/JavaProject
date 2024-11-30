package com.oxi.software.service;

import com.oxi.software.entities.Purchase;
import com.oxi.software.repository.PurchaseRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService implements Idao<Purchase, Long> {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    public Purchase findBy(Long id) {
        return this.purchaseRepository.findById(id).orElseThrow(()->
                new CustomException("Purchase with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public void save(Purchase obje) {
        this.purchaseRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<Purchase> obje) {
        this.purchaseRepository.saveAll(obje);
    }

    @Override
    public void delete(Purchase obje) {
        this.purchaseRepository.delete(obje);
    }

    @Override
    public List<Purchase> findAll() {
        return this.purchaseRepository.findAll();
    }
}
