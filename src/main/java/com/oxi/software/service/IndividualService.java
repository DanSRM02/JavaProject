package com.oxi.software.service;

import com.oxi.software.entities.Individual;
import com.oxi.software.repository.IndividualRepository;
import com.oxi.software.service.dao.Idao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndividualService implements Idao<Individual, Long> {

    @Autowired
    private IndividualRepository individualRepository;

    @Override
    public Individual getById(Long aLong) {
        return this.individualRepository.getById(aLong);
    }

    @Override
    public void save(Individual obje) {
        this.individualRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<Individual> obje) {
        this.individualRepository.saveAll(obje);
    }

    @Override
    public void delete(Individual obje) {
        this.individualRepository.delete(obje);
    }

    @Override
    public List<Individual> findAll() {
        return this.individualRepository.findAll();
    }
}
