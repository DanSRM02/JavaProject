package com.oxi.software.service;

import com.oxi.software.entities.User;
import com.oxi.software.service.dao.Idao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements Idao<User, Long> {
    @Override
    public User getById(Long aLong) {
        return null;
    }

    @Override
    public void save(User obje) {

    }

    @Override
    public void saveAll(Iterable<User> obje) {

    }

    @Override
    public void delete(User obje) {

    }

    @Override
    public List<User> findAll() {
        return List.of();
    }
}
