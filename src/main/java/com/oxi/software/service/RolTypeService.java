package com.oxi.software.service;

import com.oxi.software.entities.RolType;
import com.oxi.software.service.dao.Idao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolTypeService implements Idao<RolType, Long> {
    @Override
    public RolType getById(Long aLong) {
        return null;
    }

    @Override
    public void save(RolType obje) {

    }

    @Override
    public void saveAll(Iterable<RolType> obje) {

    }

    @Override
    public void delete(RolType obje) {

    }

    @Override
    public List<RolType> findAll() {
        return List.of();
    }
}
