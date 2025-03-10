package com.oxi.software.repository;

import com.oxi.software.entity.Review;
import com.oxi.software.repository.projection.ReviewListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<ReviewListProjection> findAllProjectedBy();

}
