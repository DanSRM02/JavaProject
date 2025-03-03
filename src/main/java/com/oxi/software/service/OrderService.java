package com.oxi.software.service;

import com.oxi.software.entity.Order;
import com.oxi.software.repository.OrderRepository;
import com.oxi.software.repository.projection.OrderDetailsProjection;
import com.oxi.software.repository.projection.OrderSummaryProjection;
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
        this.orderRepository.save(obje);
        return obje;
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

    public List<OrderSummaryProjection> findAllByState(String state) {
        return this.orderRepository.findByState(state);
    }

    public List<OrderDetailsProjection>  findOrderDetailsById(Long id) {
        return this.orderRepository.findOrderDetailsById(id);
    }
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }


}
