package com.oxi.software.service;

import com.oxi.software.entities.Review;
import com.oxi.software.service.dao.Idao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService implements Idao<Review, Long> {
    @Override
    public Review getById(Long aLong) {
        return null;
    }

    @Override
    public void save(Review obje) {

    }

    @Override
    public void saveAll(Iterable<Review> obje) {

    }

    @Override
    public void delete(Review obje) {

    }

    @Override
    public List<Review> findAll() {
        return List.of();
    }
}
