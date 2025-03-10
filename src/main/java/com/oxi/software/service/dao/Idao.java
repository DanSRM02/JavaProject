package com.oxi.software.service.dao;

import jakarta.transaction.Transactional;

import java.util.List;

public interface Idao<T,ID>{

    public T findBy(ID id);
    @Transactional
    public  T save(T obje);

    @Transactional
    public void saveAll(Iterable<T> obje);

    @Transactional
    public void delete(T obje);

    // Para obtener una lista de todas las entidades con paginación
    List<T> findAll();
}
