package com.oxi.software.service;

import com.oxi.software.entities.Order;
import com.oxi.software.entities.Unit;
import com.oxi.software.repository.UnitRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService implements Idao<Unit, Long> {

    @Autowired
    private UnitRepository unitRepository;

    @Override
    public Unit findBy(Long id) {
        return this.unitRepository.findById(id).orElseThrow(()->
                new CustomException("Unit with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public Order save(Unit obje) {
        this.unitRepository.save(obje);
        return null;
    }

    @Override
    public void saveAll(Iterable<Unit> obje) {
        this.unitRepository.saveAll(obje);
    }

    @Override
    public void delete(Unit obje) {
        this.unitRepository.delete(obje);
    }

    @Override
    public List<Unit> findAll() {
        return this.unitRepository.findAll();
    }
}
