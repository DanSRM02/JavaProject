package com.oxi.software.service;

import com.oxi.software.entity.Individual;
import com.oxi.software.entity.User;
import com.oxi.software.repository.IndividualRepository;
import com.oxi.software.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean existsUser(String username){
        return userRepository.existsByUsername(username);
    }

}
