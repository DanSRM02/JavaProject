package com.oxi.software.service;

import com.oxi.software.entities.Order;
import com.oxi.software.repository.OrderRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements Idao<Order, Long> {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order findBy(Long id) {
        return this.orderRepository.findById(id).orElseThrow(()->
                new CustomException("Order with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public Order save(Order obje) {
        return this.orderRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<Order> obje) {
        this.orderRepository.saveAll(obje);
    }

    @Override
    public void delete(Order obje) {
        this.orderRepository.delete(obje);
    }

    @Override
    public List<Order> findAll() {
        return this.orderRepository.findAll();
    }
}
