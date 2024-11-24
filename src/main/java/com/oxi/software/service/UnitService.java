package com.oxi.software.service;

import com.oxi.software.entities.Unit;
import com.oxi.software.repository.UnitRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnitService implements Idao<Unit, Long> {

    @Autowired
    private UnitRepository unitRepository;

    @Override
    public Unit getById(Long id) {
        return this.unitRepository.findById(id).orElseThrow(()->
                new CustomException("Product with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public void save(Unit obje) {

    }

    @Override
    public void saveAll(Iterable<Unit> obje) {

    }

    @Override
    public void delete(Unit obje) {

    }

    @Override
    public List<Unit> findAll() {
        return List.of();
    }
}
