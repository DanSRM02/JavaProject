package com.oxi.software.service;

import com.oxi.software.entities.Unit;
import com.oxi.software.service.dao.Idao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService implements Idao<Unit, Long> {
    @Override
    public Unit getById(Long aLong) {
        return null;
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
