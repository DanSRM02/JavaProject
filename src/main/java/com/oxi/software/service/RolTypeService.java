package com.oxi.software.service;

import com.oxi.software.entity.RolType;
import com.oxi.software.repository.RolTypeRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolTypeService implements Idao<RolType, Long> {

    @Autowired
    private RolTypeRepository rolTypeRepository;

    @Override
    public RolType findBy(Long id) {
        return this.rolTypeRepository.findById(id).orElseThrow(()->
                new CustomException("Rol Type with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public void save(RolType obje) {
        this.rolTypeRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<RolType> obje) {
        this.rolTypeRepository.saveAll(obje);
    }

    @Override
    public void delete(RolType obje) {
        this.rolTypeRepository.delete(obje);
    }

    @Override
    public List<RolType> findAll() {
        return this.rolTypeRepository.findAll();
    }
}
