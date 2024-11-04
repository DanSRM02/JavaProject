package com.oxi.software.service;

import com.oxi.software.entities.Purchase;
import com.oxi.software.repository.PurchaseRepository;
import com.oxi.software.service.dao.Idao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService implements Idao<Purchase, Long> {
    @Autowired
    private PurchaseRepository purchaseRepository;


    @Override
    public Purchase getById(Long aLong) {
        return this.purchaseRepository.getById(aLong);
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
