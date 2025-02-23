package com.oxi.software.service;

import com.oxi.software.entity.Review;
import com.oxi.software.repository.ReviewRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService implements Idao<Review, Long> {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review findBy(Long id) {
        return this.reviewRepository.findById(id).orElseThrow(()->
                new CustomException("Review with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public void save(Review obje) {
        this.reviewRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<Review> obje) {
        this.reviewRepository.saveAll(obje);
    }

    @Override
    public void delete(Review obje) {
        this.reviewRepository.delete(obje);
    }

    @Override
    public List<Review> findAll() {
        return this.reviewRepository.findAll();
    }
}
