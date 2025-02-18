package com.oxi.software.service;

import com.oxi.software.entities.Order;
import com.oxi.software.entities.User;
import com.oxi.software.repository.UserRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements Idao<User, Long> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findBy(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
            new CustomException("User with id "+ id + " Not Found ", HttpStatus.NO_CONTENT));
    }

    @Override
    public Order save(User obje) {
        this.userRepository.save(obje);
        return null;
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveAll(Iterable<User> obje) {
        this.userRepository.saveAll(obje);
    }

    @Override
    public void delete(User obje) {
        this.userRepository.delete(obje);
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }
}
