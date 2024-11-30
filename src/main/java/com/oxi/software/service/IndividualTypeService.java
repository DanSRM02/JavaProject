package com.oxi.software.service;

import com.oxi.software.entities.IndividualType;
import com.oxi.software.repository.IndividualTypeRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndividualTypeService implements Idao<IndividualType, Long> {

    @Autowired
    private IndividualTypeRepository individualTypeRepository;

    @Override
    public IndividualType findBy(Long id) {
        return this.individualTypeRepository.findById(id).orElseThrow(()->
                new CustomException("Individual type with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public void save(IndividualType obje) {
        this.individualTypeRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<IndividualType> obje) {
        this.individualTypeRepository.saveAll(obje);
    }

    @Override
    public void delete(IndividualType obje) {
        this.individualTypeRepository.delete(obje);
    }

    @Override
    public List<IndividualType> findAll() {
        return this.individualTypeRepository.findAll();
    }
}
